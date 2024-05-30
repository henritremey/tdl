/**
 * 
 */
package fr.n7.stl.block.ast.type;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.classe.ClassDeclaration;

/**
 
Represents the inheritance relationships for a class.*/
public class Inheritance {
    private ClassDeclaration parentClass;

    public Inheritance(ClassDeclaration _parentClass) {
        this.parentClass = _parentClass;
    }

    public ClassDeclaration getParentClass() {
        return this.parentClass;
    }

    public void setParentClass(ClassDeclaration _parentClass) {
        this.parentClass = _parentClass;
    }

    @Override
    public String toString() {
        return "Inherits from " + (parentClass != null ? parentClass.getName() : "None");
    }
}