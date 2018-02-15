# Configure build library name.
set(TARGET_NAME dlib_jni)
# Configure install folder for:
# 1) Finding the dependent libraries.
# 2) Copy out the generated files for others to use.
set(INSTALL_DIR ${CMAKE_SOURCE_DIR}/../../../../lib-distribution/)
# Configure import libs.
set(LIB_DIR ${CMAKE_SOURCE_DIR}/../cppLibs)
# Configure the main build target (JNI wrapper).
add_library(${TARGET_NAME} SHARED
            dlib-jni.cpp
            include/my/dlib/data/messages.pb.cc)
target_include_directories(${TARGET_NAME} PRIVATE
                           ${INSTALL_DIR}/protobuf_jni/include
                           ${CMAKE_SOURCE_DIR}/include
                           ${LIB_DIR}/dlib/include)
target_link_libraries(${TARGET_NAME}
                      dlib
                      protobuf
                      android
                      jnigraphics
                      log)