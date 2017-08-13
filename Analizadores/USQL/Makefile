all: compilar

generar:	GeneradorUSQL.jj
	javacc GeneradorUSQL.jj

compilar: generar
	javac analizador/*.java
