/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.BinaryOperator;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a cell in an array.
 * @author Marc Pantel
 */
public class ArrayAssignment extends AbstractArray implements AssignableExpression {

	/**
	 * Construction for the implementation of an array element assignment expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element assignment expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element assignment expression.
	 */
	public ArrayAssignment(AssignableExpression _array, Expression _index) {
		super(_array, _index);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.ArrayAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment f = _factory.createFragment();
		
		VariableDeclaration array =((VariableAssignment) this.array).getDeclaration();
		
		f.add(_factory.createLoad(array.getRegister(), array.getOffset(), this.getType().length()) );//le on charge le tableau (ie l'adresse 
																									//du tableau est en sommet de pile)
		f.append(this.index.getCode(_factory));
		f.add(_factory.createLoadL(array.getType().length()));
		f.add(TAMFactory.createBinaryOperator(BinaryOperator.Multiply));
		f.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
		
		//à ce stade l'adresse en tête de liste est l'adresse de l'assignable (case du tableau) 
		// dans lequel on doit stocker notre valeur
		
		f.add(_factory.createStoreI(this.getType().length()));
		
		
		return f;
		
		
		

		
		
		
		
		
	}

	
}
