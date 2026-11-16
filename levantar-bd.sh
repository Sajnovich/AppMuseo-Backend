#!/bin/bash
# Enciende MySQL en Docker y espera a que esté listo para recibir conexiones
cd "$(dirname "$0")"

if ! docker info > /dev/null 2>&1; then
  echo "Docker no está corriendo. Abre Docker Desktop y vuelve a intentar."
  exit 1
fi

docker compose up -d

echo -n "Esperando a MySQL"
for i in $(seq 1 40); do
  estado=$(docker inspect -f '{{.State.Health.Status}}' appmuseo-mysql 2>/dev/null)
  if [ "$estado" = "healthy" ]; then
    echo ""
    echo "MySQL listo en 127.0.0.1:3306 (usuario root / clave museo2026)"
    echo "Bases: museo_usuarios, museo_catalogo, museo_pedidos"
    exit 0
  fi
  echo -n "."
  sleep 3
done
echo ""
echo "MySQL no respondió a tiempo. Revisa con: docker compose logs mysql"
exit 1
