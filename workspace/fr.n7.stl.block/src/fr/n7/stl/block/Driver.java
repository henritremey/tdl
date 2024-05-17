package fr.n7.stl.block;

import java.io.File;

class Driver {

	public static void main(String[] args) throws Exception {
		File dossier = new File("/home/grinaldi/2A/TLA/tdl/workspace/fr.n7.stl.block/testsMoodle");
		File[] f = dossier.listFiles();
		for (int i = 0; i< f.length; i++) {
			Parser parser = null;
			if (args.length == 0) {
				parser = new Parser(f[i].getAbsolutePath());
				parser.parse();
				System.out.println("==============================================");
			} 
			else {
				for (String name : args) {
					parser = new Parser( name );
					parser.parse();
				}
			}
		}
	}
	
}