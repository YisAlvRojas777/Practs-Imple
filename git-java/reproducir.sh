#!/usr/bin/env bash
# Reproduce los pasos 2 a 8 del ejercicio en un repositorio temporal
# (no modifica los archivos de esta carpeta) y muestra la salida de cada comando.
# Uso: bash reproducir.sh
set -u
ORIGEN="$(cd "$(dirname "$0")" && pwd)"
TRABAJO="$(mktemp -d)"
cp -r "$ORIGEN/README.md" "$ORIGEN/.gitignore" "$ORIGEN/src" "$TRABAJO/"
cd "$TRABAJO"

git init -q -b main
git config user.name  >/dev/null || git config user.name  "Estudiante"
git config user.email >/dev/null || git config user.email "estudiante@example.com"

run() { echo "\$ $*"; "$@" 2>&1; }
titulo() { echo; echo "=================== $1 ==================="; }

titulo "2. Compilar y ejecutar los ejemplos"
mkdir -p bin
run javac -d bin src/Saludo.java src/Calculadora.java
run java -cp bin Saludo
run java -cp bin Calculadora
echo "-- Modifico el mensaje de Saludo.java y vuelvo a compilar --"
sed -i 's/Hola desde el Dev Container/Hola desde el Dev Container con Git/' src/Saludo.java
run javac -d bin src/Saludo.java
run java -cp bin Saludo

titulo "3. Preparar el repositorio"
run git status --short
run git add README.md .gitignore src/Saludo.java src/Calculadora.java
run git diff --cached --stat
echo "\$ git diff --cached --name-only | grep -c '\.class'   (debe ser 0)"
git diff --cached --name-only | grep -c '\.class'

titulo "4. Crear y corregir commits"
run git commit -m "Agrega ejercicio de Git y Java"
run git log --oneline -1
sed -i 's/Completa las actividades en orden/Completa las actividades en el orden indicado/' README.md
run git diff
run git add README.md
run git diff --cached --stat
run git commit --amend --no-edit
run git log --oneline -1
run git show --stat --oneline HEAD

titulo "5. Rama y merge"
run git switch -c mejora-instrucciones
echo "" >> README.md
echo "> Aclaración: ejecuta los comandos desde la raíz del repositorio." >> README.md
run git add README.md
run git commit -m "Aclara las instrucciones del ejercicio"
run git switch main
run git merge mejora-instrucciones
run git log --oneline --all --decorate

titulo "6. Remoto"
run git remote -v
run git branch --all
echo "(Repositorio de práctica sin remoto: se intenta push y pull)"
run git push -u origin main
run git pull

titulo "7. Parche"
git diff HEAD^ HEAD > cambio.patch
echo "\$ git diff HEAD^ HEAD > cambio.patch"
run head -n 25 cambio.patch
git diff > cambios-sin-commit.patch
echo "\$ git diff > cambios-sin-commit.patch   ($(wc -c < cambios-sin-commit.patch) bytes: no hay cambios sin preparar)"
cp cambio.patch "$ORIGEN/cambio.patch"

titulo "8. Comprobar .gitignore"
touch ejemplo.bin ejemplo.javac
run git check-ignore -v bin/Saludo.class ejemplo.bin ejemplo.javac
run git status --ignored --short
run git status --short
echo "(sin salida arriba = no hay archivos sin seguimiento ni cambios pendientes)"

titulo "Lista final"
run git status
run git log --oneline
echo
echo "Repositorio temporal usado: $TRABAJO"
