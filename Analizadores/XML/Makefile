all: compilar

generar:	GeneradorXML.jj
	javacc GeneradorXML.jj

compilar: generar
	javac analizador/*.java
