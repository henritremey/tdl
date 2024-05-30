package fr.n7.stl.block.ast.classe;

public enum AccessRight {
	Private,
	Public,
	Protected;

	public String toString() {
		switch(this) {
		case Public:
			return "public";			
		case Private:
			return "private";
		case Protected:
			return "protected";
		default:
			throw new IllegalArgumentException("Non reconnu");
		}
	}
}
