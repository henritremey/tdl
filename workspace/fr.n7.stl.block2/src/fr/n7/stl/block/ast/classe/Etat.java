package fr.n7.stl.block.ast.classe;

public enum Etat {

    None,
    Static,
    Final,
    StaticFinal;

    public String toString() {
        switch (this) {
            case None:
                return "";
            case Static:
                return "static ";
            case Final:
                return "final ";
            case StaticFinal:
                return "static final ";
            default:
                throw new IllegalArgumentException("Non connu");
        }
    }

}
