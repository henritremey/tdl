package fr.n7.stl.block;

class Driver {

	public static void main(String[] args) throws Exception {
		Parser parser = null;
		if (args.length == 0) {
			parser = new Parser( "/home/grinaldi/2A/TLA/tdl/workspace/fr.n7.stl.block/tests/MiniJ/input.txt" );
			parser.parse();
		} else {
			for (String name : args) {
				parser = new Parser( name );
				parser.parse();
			}
		}
	}
	
}