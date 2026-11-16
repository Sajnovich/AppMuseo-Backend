#!/bin/bash
# BORRA todas las tablas y datos y deja MySQL como recién instalado.
# Al volver a arrancar los microservicios se cargan de nuevo los datos del caso.
cd "$(dirname "$0")"
read -p "Esto borra TODOS los datos de las tres bases. ¿Seguro? (s/n) " respuesta
if [ "$respuesta" = "s" ]; then
  docker compose down -v
  ./levantar-bd.sh
else
  echo "Cancelado."
fi
