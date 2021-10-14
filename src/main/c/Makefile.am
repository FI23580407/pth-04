pkglib_LTLIBRARIES = FSlognorm.la

FSlognorm_la_SOURCES = FSlognorm.c
FSlognorm_la_CPPFLAGS = -I$(top_srcdir) $(PTHREADS_CFLAGS) $(RSRT_CFLAGS) $(JAVA_INCLUDES)
FSlognorm_la_LDFLAGS = -module -avoid-version $(JAVA_LIBS)
FSlognorm_la_LIBADD = $(RSRT_LIBS)
