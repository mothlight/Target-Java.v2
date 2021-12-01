cd ../classes/
javac -d . -cp ../netcdfAll-4.6.11.jar:../slf4j-jdk14-1.7.14.jar  ../src/Target/*.java ../src/Target/HTC/*.java

java -cp ./:../netcdfAll-4.6.11.jar:../slf4j-jdk14-1.7.14.jar  Target.RunToolkit ../example/AU-Preston/AU-Preston_control.txt


