# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.




add_library( # Sets the name of the library.
             NE10
              # static 就是a文件，shared 就是so文件
             STATIC
              # 这里提供的是预编译好的文件，所以用这个imported，
              # 否则需要提供需要编译文件的列表
             IMPORTED
         )

set_target_properties(
           NE10
  # 上面的PNG库是预编译的，这里的属性表示文件所在的位置
          PROPERTIES IMPORTED_LOCATION
   # 提供预编译文件的位置。 CMAKE_SOURCE_DIR 是内置变量表示当前CMakeLists.txt的位置。
          ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libNE10.a )
add_library( # Sets the name of the library.
             NE10_test_demo
             SHARED
              src/main/cpp/NE10_test_demo.cpp
              )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.



# 表示编译文件时候，头文件的位置。路径是相对于当前文件的
include_directories(
${CMAKE_SOURCE_DIR}/libs/include
 )




target_link_libraries( # Specifies the target library.
                       NE10_test_demo
                       NE10
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                      )