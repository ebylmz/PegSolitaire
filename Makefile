JC = javac
R = java
Packet = 
OUT_DIR = 
JFlags =  

muo:
	$(JC) $(JFlags) -d $(OUT_DIR) src/PegSolitaire.java src/TestPegSolitaire.java
#$(R) $(OUT_DIR)/TestPegSolitaire

javadoc:
	javadoc -d doc/ $(Packet)/*.java

rebuild: clean muo

clean:
	rm -rf $(OUT_DIR)/*.class doc/ 