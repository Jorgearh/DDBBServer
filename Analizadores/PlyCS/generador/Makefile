all: compilar

generar:	GeneradorPlyCS.jj
	javacc GeneradorPlyCS.jj

compilar: generar
	javac *.java
