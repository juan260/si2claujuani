#!/bin/bash
export J2EE_HOME=/opt/glassfish4/glassfish;
for i in P1-base P1-ws P1-ejb-servidor-remoto P1-ejb-cliente-remoto; do
  cd $i
  ant replegar; ant delete-pool-local
  cd -
done
cd P1-base
ant delete-db
cd -
for i in P1-base P1-ejb-servidor-remoto P1-ejb-cliente-remoto P1-ws; do
  cd $i
  ant limpiar-todo unsetup-db todo
  cd -
done
