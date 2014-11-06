#/bin/bash!

cf api --skip-ssl-validation https://api.10.244.0.34.xip.io
cf auth admin admin
cf create-org me
cf target -o me
cf create-space development
cf target -s development

