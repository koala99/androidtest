/*
 *  Copyright 2013-16 ARM Limited and Contributors.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of ARM Limited nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY ARM LIMITED AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL ARM LIMITED AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
extern "C" {
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <android/log.h>
#include <sys/time.h>
#include <time.h>

#include "NE10.h"
#include "NE10_math.h"
#include "NE10_types.h"

//also use the Neon framework
#include <arm_neon.h>
}

#define LOGV(...) __android_log_print(ANDROID_LOG_SILENT, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_ne10_demo_MainActivity_NE10RunTest(JNIEnv *env,
                                                jobject thiz) {
    struct timeval start, end;
    long stpElapsed, noStpElapsed, autoElapsed, neonElapsed;
    long avgStpElapsed, avgNoStpElapsed, avgAutoElapsed, avgNeonElapsed;

    char rtStr[2048];
    //const int NUM_ELEM = 32768; //32kB is L1 cache (for Samsung Exynos 4412)
    const int NUM_ELEM = 4096;
    const int STP_SIZE = 4;
    const int AVG_PASS = 1000;

    //do a little bit of simple float arithmetric (vector by scalar)
//    float *src = malloc(sizeof(float) * NUM_ELEM); //eight floats

    float *src= (float *) malloc(sizeof(float)*NUM_ELEM);
    float *dest = (float *) malloc(sizeof(float) * NUM_ELEM);

    //initialise the normal counters
    stpElapsed = 0;
    noStpElapsed = 0;
    autoElapsed = 0;
    neonElapsed = 0;

    //initialise the average counters
    avgAutoElapsed = 0;
    avgNoStpElapsed = 0;
    avgStpElapsed = 0;
    avgNeonElapsed = 0;

    for (int j = 0; j < AVG_PASS; j++) {
        //set input array
        for (int i = 0; i < NUM_ELEM; i++) {
            src[i] = i;
        }

        //time it as a whole (no stepping)
        gettimeofday(&start, NULL);
        for (int i = 0; i < NUM_ELEM; i++) {
            dest[i] = src[i] + 1;
        }
        gettimeofday(&end, NULL);
        autoElapsed = (end.tv_sec - start.tv_sec) * 1000000 + end.tv_usec - start.tv_usec;

        //reset input array
        for (int i = 0; i < NUM_ELEM; i++) {
            src[i] = i;
        }

        //time it as a whole (no stepping)
        gettimeofday(&start, NULL);
        ne10_addc_float_c(dest, src, 2.0f, NUM_ELEM);
        gettimeofday(&end, NULL);
        noStpElapsed = (end.tv_sec - start.tv_sec) * 1000000 + end.tv_usec - start.tv_usec;


        //reset input array
        for (int i = 0; i < NUM_ELEM; i++) {
            src[i] = i;
        }

        //time it stepping
        gettimeofday(&start, NULL);
        for (int i = 0; i < NUM_ELEM; i += STP_SIZE) {
            ne10_addc_float_c(dest + i + STP_SIZE, src + i + STP_SIZE, 2.0f, STP_SIZE);
        }
        gettimeofday(&end, NULL);
        stpElapsed = (end.tv_sec - start.tv_sec) * 1000000 + end.tv_usec - start.tv_usec;

        //time it using neon
        gettimeofday(&start, NULL);
        for (int i = 0; i < NUM_ELEM; i += 4) {
            float32x4_t src128 = vld1q_f32((const float32_t *) (src + i));
            float32x4_t tmp128 = vdupq_n_f32(2.0f);
            float32x4_t dst128 = vaddq_f32(src128, tmp128);
            vst1q_f32((float32_t *) (dest + i), dst128);
        }
        gettimeofday(&end, NULL);
        neonElapsed = (end.tv_sec - start.tv_sec) * 1000000 + end.tv_usec - start.tv_usec;

        avgAutoElapsed += autoElapsed;
        avgNoStpElapsed += noStpElapsed;
        avgStpElapsed += stpElapsed;
        avgNeonElapsed += neonElapsed;
    }

    //average our runtimes
    avgAutoElapsed /= AVG_PASS;
    avgNoStpElapsed /= AVG_PASS;
    avgStpElapsed /= AVG_PASS;
    avgNeonElapsed /= AVG_PASS;

    //return the good news!
    sprintf(rtStr,
            "Auto\t\t\t\ttook %d \nNe10\tnostep:\ttook %d \n\t\t\tstep:\ttook %d\nNEON\t\t\t\ttook %d microseconds\n",
            avgAutoElapsed, avgNoStpElapsed, avgStpElapsed, avgNeonElapsed);
    return (env)->NewStringUTF(rtStr);
}
}
