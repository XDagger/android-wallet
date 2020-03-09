//
//  utils.c
//  xdag
//
//  Copyright © 2018 xdag contributors.
//

#include "utils.h"

size_t validate_remark(const char *str)
{
	return validate_ascii_safe(str, 33);// sizeof(xdag_remark_t) + 1
}

size_t validate_ascii_safe(const char *str, size_t maxsize)
{
	if(str == NULL) {
		return 0;
	}

	const char* start = str;
	const char* stop = str + maxsize;

	for(;str < stop;++str) {
		if(*str < 32 || *str > 126) {
			if(*str == '\0') {
				return str - start;
			}
			return 0;
		}
	}

	return 0;
}
