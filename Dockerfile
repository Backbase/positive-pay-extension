FROM harbor.backbase.eu/staging/positive-pay:${DBS_VERSION}

ARG JAR_FILE
COPY target/${JAR_FILE} /app/WEB-INF/lib/