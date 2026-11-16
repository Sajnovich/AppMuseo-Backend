#!/bin/bash
# Apaga MySQL sin borrar los datos
cd "$(dirname "$0")"
docker compose stop
echo "MySQL detenido. Los datos siguen guardados; para volver a encenderlo: ./levantar-bd.sh"
