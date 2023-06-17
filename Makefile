# the compiler: JC for Java
JFLAGS = -g
JC = javac
JVM = java
WINREMOVE = del

TARGET = s40162788_detector



# Clear any default targets for building .class files from .java files
# Currently, clearing the default for .java.class is not necessary since 
.SUFFIXES: .java .class

.java.class:
	@$(JC) $(JFLAGS) $*.java

CLASSES = \
	$(TARGET).java

all: $(TARGET)
	
default: classes

classes: $(CLASSES:.java=.class)

run: $(TARGET).class
	@$(JVM) $(TARGET) $(FILE1) $(FILE2)
	
clean:
	$(RM) *.class

cleanclass:
	$(WINREMOVE) *.class
