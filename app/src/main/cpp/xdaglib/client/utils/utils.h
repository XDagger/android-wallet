//
//  utils.h
//  xdag
//
//  Copyright © 2018 xdag contributors.
//

#ifndef XDAG_UTILS_HEADER_H
#define XDAG_UTILS_HEADER_H

#include <stdint.h>

#ifdef _WIN32
#define DELIMITER "\\"
#else
#define DELIMITER "/"
#endif

#ifdef __cplusplus
extern "C" {
#endif

size_t validate_remark(const char *str);
size_t validate_ascii_safe(const char *str, size_t);

#ifdef __cplusplus
};
#endif

#endif /* utils_h */
