package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.List;
import fr.n7.stl.block.ast.classe.AttributeDeclaration;
import fr.n7.stl.block.ast.classe.ClassElement;
import fr.n7.stl.block.ast.classe.ConstructorDeclaration;
import fr.n7.stl.block.ast.classe.MethodDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.Instance;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class ClassDeclaration implements Element, Declaration {
    private String name;
    private List<ClassElement> classElements;
    private SymbolTable elementsTable;

    public ClassDeclaration(String _name, List<ClassElement> _classElements) {
        this.name = _name;
        this.classElements = new ArrayList<>(_classElements);
    }
    public List<ClassElement> getClassElements() {
        return this.classElements;
    }
    public List<AttributeDeclaration> getClassAttributes() {
        List<AttributeDeclaration> attributes = new ArrayList<>();
        for (ClassElement element : classElements) {
            if (element instanceof AttributeDeclaration) {
                attributes.add((AttributeDeclaration) element);
            }
        }
        return attributes;
    }

    public List<MethodDeclaration> getClassMethods() {
        List<MethodDeclaration> methods = new ArrayList<>();
        for (ClassElement element : classElements) {
            if (element instanceof MethodDeclaration) {
                methods.add((MethodDeclaration) element);
            }
        }
        return methods;
    }

    public List<ConstructorDeclaration> getClassConstructors() {
        List<ConstructorDeclaration> constructors = new ArrayList<>();
        for (ClassElement element : classElements) {
            if (element instanceof ConstructorDeclaration) {
                constructors.add((ConstructorDeclaration) element);
            }
        }
        return constructors;
    }

    public SymbolTable getElementsTable() {
        return this.elementsTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("class ").append(this.name).append(" {\n");
        classElements.forEach(element -> builder.append("\t").append(element.toString()).append("\n"));
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {
        if (_scope.accepts(this)) {
            _scope.register(this);
            this.elementsTable = new SymbolTable(_scope);
            for (ClassElement element : classElements) {
                if (!element.collectAndBackwardResolve(elementsTable)) {
                    return false;
                }
            }
            return true;
        } else {
            Logger.error("The class identifier " + this.name + " is already defined.");
            return false;
        }
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        for (ClassElement element : classElements) {
            if (!element.fullResolve(elementsTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkType() {
        for (ClassElement element : classElements) {
            if (!element.checkType()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        int localOffset = _offset;
        for (AttributeDeclaration attribute : getClassAttributes()) {
            localOffset += attribute.getType().length();
        }
        return localOffset - _offset;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment fragment = _factory.createFragment();
        classElements.forEach(element -> fragment.append(element.getCode(_factory)));
        return fragment;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return new Instance(name, this);
    }





}
