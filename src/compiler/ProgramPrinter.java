package compiler;

import gen.MiniJavaListener;
import gen.MiniJavaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

public class ProgramPrinter implements MiniJavaListener {
    public static int indent_level = 0;
    private boolean nestedBlockForStatement = false;
    private Stack<Boolean> nestedBlockStack = new Stack<Boolean>();

    private String typeCheck(MiniJavaParser.TypeContext miniJavaDataType){
        String type;

        if (miniJavaDataType.javaType() != null){
            type = (miniJavaDataType.javaType().getText().equals("number")) ? "int" : miniJavaDataType.javaType().getText() ;
        }else {
            type = (miniJavaDataType.Identifier().getText().equals("number")) ? "int" : miniJavaDataType.Identifier().getText() ;
        }

        if (miniJavaDataType.LSB() != null)
            type = type.concat(miniJavaDataType.LSB().getText());
        if (miniJavaDataType.RSB() != null)
            type = type.concat(miniJavaDataType.RSB().getText());

        return type;
    }

    private String interfaceMethodSignature (MiniJavaParser.InterfaceMethodDeclarationContext methodNode){
        String str = "\t";

        if(methodNode.accessModifier() != null){
            str = str.concat(methodNode.accessModifier().getText() + " ");
        }
        if (methodNode.returnType().type() == null){
            str = str.concat(methodNode.returnType().getText() + " ");
        }else{
            str = str.concat(this.typeCheck(methodNode.returnType().type()) + " ");
        }

        str = str.concat(methodNode.Identifier().getText() + " (");
        //System.out.println(methodNode.parameterList().parameter().get(0).type().getText());
        if (methodNode.parameterList() != null) {
            for (int i = 0; i <= methodNode.parameterList().parameter().size() - 1; i++) {
                str = str.concat(this.typeCheck(methodNode.parameterList().parameter().get(i).type()) + " " + methodNode.parameterList().parameter().get(i).Identifier());
                if (i != methodNode.parameterList().parameter().size() - 1) {
                    str = str.concat(", ");
                } else {
                    str = str.concat(");\n");
                }
            }
        }else{
            str = str.concat(");\n");
        }

        return str;
    }

    private String classMethodSignature (MiniJavaParser.MethodDeclarationContext methodNode){
        String str = "\t";

        if (methodNode.Override() != null){
            str = str.concat(methodNode.Override().getText() + "\n\t");
        }

        if(methodNode.accessModifier() != null){
            str = str.concat(methodNode.accessModifier().getText() + " ");
        }
        if (methodNode.returnType().type() == null){
            str = str.concat(methodNode.returnType().getText() + " ");
        }else{
            str = str.concat(this.typeCheck(methodNode.returnType().type()) + " ");
        }

        str = str.concat(methodNode.Identifier().getText() + " (");
        //System.out.println(methodNode.parameterList().parameter().get(0).type().getText());
        if (methodNode.parameterList() != null) {
            for (int i = 0; i <= methodNode.parameterList().parameter().size() - 1; i++) {
                str = str.concat(this.typeCheck(methodNode.parameterList().parameter().get(i).type()) + " " + methodNode.parameterList().parameter().get(i).Identifier());
                if (i != methodNode.parameterList().parameter().size() - 1) {
                    str = str.concat(", ");
                } else {
                    str = str.concat(") {\n");
                }
            }
        }else{
            str = str.concat(") {\n");
        }

        return str;
    }

    private void tabPrint(int tabCount){
        for (int i=0; i<tabCount; i++)
            System.out.print("\t");
    }

    private String getExpression(MiniJavaParser.ExpressionContext expressionNode){
        if (expressionNode.start.getText().equals("new")){
            return expressionNode.getText().replace("new", "new ")  ;
        }else {
            return expressionNode.getText();
        }
    }

    @Override
    public void enterProgram(MiniJavaParser.ProgramContext ctx) {

    }

    @Override
    public void exitProgram(MiniJavaParser.ProgramContext ctx) {

    }

    @Override
    public void enterMainClass(MiniJavaParser.MainClassContext ctx) {
        System.out.print("class " + ctx.Identifier() + " {\n");
        indent_level += 1;
    }

    @Override
    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        System.out.print("}\n");
        indent_level -= 1;
    }

    @Override
    public void enterMainMethod(MiniJavaParser.MainMethodContext ctx) {
        indent_level += 1;
        String str = "\tpublic static void main (";
        str = str.concat(this.typeCheck(ctx.type()));

        System.out.print(str + " " + ctx.Identifier() + "){\n");

    }

    @Override
    public void exitMainMethod(MiniJavaParser.MainMethodContext ctx) {
        indent_level -= 1;
        System.out.print("\t}\n");
    }

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        String str = "class " + ctx.className.getText() ;
        int interfaces_index;

        if (ctx.parentName != null){
            str = str.concat(" extends " + ctx.parentName.getText());
            interfaces_index = 2;
        }else{
            interfaces_index = 1;
        }

        if (ctx.getText().contains("implements")){
            str = str.concat(" implements ");
            for (int i=interfaces_index; i<=ctx.Identifier().size()-1; i++){
                str = str.concat(ctx.Identifier().get(i).toString());
                if (i != ctx.Identifier().size()-1){
                    str = str.concat(", ");
                }else{
                    str = str.concat("{\n");

                }
            }
        }else{
            str = str.concat("{\n");
        }
        System.out.print(str);
        indent_level += 1;
    }

    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        System.out.print("}\n");
        indent_level -= 1;
    }

    @Override
    public void enterInterfaceDeclaration(MiniJavaParser.InterfaceDeclarationContext ctx) {
        String str = "interface " + ctx.Identifier().getText() + " {\n";
        System.out.print(str);
        indent_level += 1;
    }

    @Override
    public void exitInterfaceDeclaration(MiniJavaParser.InterfaceDeclarationContext ctx) {
        System.out.print("}\n");
        indent_level -= 1;
    }

    @Override
    public void enterInterfaceMethodDeclaration(MiniJavaParser.InterfaceMethodDeclarationContext ctx) {
        System.out.print(interfaceMethodSignature(ctx));
    }

    @Override
    public void exitInterfaceMethodDeclaration(MiniJavaParser.InterfaceMethodDeclarationContext ctx) {

    }

    @Override
    public void enterFieldDeclaration(MiniJavaParser.FieldDeclarationContext ctx) {
        String str = "\t" ;

        if (ctx.accessModifier() != null)
            str = str.concat(ctx.accessModifier().getText() + " ");
        if (ctx.Final() != null)
            str = str.concat(ctx.Final().getText() + " ");
        str = str.concat(typeCheck(ctx.type()) + " " + ctx.Identifier() + " ");

        if (ctx.EQ() != null){
            str = str.concat(ctx.EQ().getText() + " " + this.getExpression(ctx.expression()) + " ;\n");
        }else{
            str = str.concat(";\n");
        }

        System.out.print(str);
    }

    @Override
    public void exitFieldDeclaration(MiniJavaParser.FieldDeclarationContext ctx) {

    }

    @Override
    public void enterLocalDeclaration(MiniJavaParser.LocalDeclarationContext ctx) {
        tabPrint(indent_level);
        System.out.println(this.typeCheck(ctx.type()) + " " + ctx.Identifier() + ";");
    }

    @Override
    public void exitLocalDeclaration(MiniJavaParser.LocalDeclarationContext ctx) {

    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        System.out.print(classMethodSignature(ctx));
        indent_level += 1;
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        if (ctx.methodBody().RETURN() != null)
            System.out.println("\t\treturn " + ctx.methodBody().expression().getText() + ";");
        System.out.print("\t}\n");
        indent_level -= 1;
    }

    @Override
    public void enterParameterList(MiniJavaParser.ParameterListContext ctx) {

    }

    @Override
    public void exitParameterList(MiniJavaParser.ParameterListContext ctx) {

    }

    @Override
    public void enterParameter(MiniJavaParser.ParameterContext ctx) {

    }

    @Override
    public void exitParameter(MiniJavaParser.ParameterContext ctx) {

    }

    @Override
    public void enterMethodBody(MiniJavaParser.MethodBodyContext ctx) {

    }

    @Override
    public void exitMethodBody(MiniJavaParser.MethodBodyContext ctx) {

    }

    @Override
    public void enterType(MiniJavaParser.TypeContext ctx) {

    }

    @Override
    public void exitType(MiniJavaParser.TypeContext ctx) {

    }

    @Override
    public void enterBooleanType(MiniJavaParser.BooleanTypeContext ctx) {

    }

    @Override
    public void exitBooleanType(MiniJavaParser.BooleanTypeContext ctx) {

    }

    @Override
    public void enterReturnType(MiniJavaParser.ReturnTypeContext ctx) {

    }

    @Override
    public void exitReturnType(MiniJavaParser.ReturnTypeContext ctx) {

    }

    @Override
    public void enterAccessModifier(MiniJavaParser.AccessModifierContext ctx) {

    }

    @Override
    public void exitAccessModifier(MiniJavaParser.AccessModifierContext ctx) {

    }

    @Override
    public void enterNestedStatement(MiniJavaParser.NestedStatementContext ctx) {
        if (!nestedBlockForStatement) {
            tabPrint(indent_level);
            indent_level += 1;
        }
        nestedBlockStack.push(nestedBlockForStatement);
        nestedBlockForStatement = false;
        System.out.println("{");
    }

    @Override
    public void exitNestedStatement(MiniJavaParser.NestedStatementContext ctx) {
        boolean status = nestedBlockStack.pop();
        if (! status) {
            indent_level -= 1;
            tabPrint(indent_level);
        }else{
            tabPrint(indent_level-1);
        }
        System.out.println("}");
    }

    @Override
    public void enterIfElseStatement(MiniJavaParser.IfElseStatementContext ctx) {
        tabPrint(indent_level);
        String str = "if " + ctx.LP() + " " + ctx.expression().getText() + " " + ctx.RP() + " " ;
        System.out.print(str);
        indent_level += 1;
    }

    @Override
    public void exitIfElseStatement(MiniJavaParser.IfElseStatementContext ctx) {
//        indent_level -= 1;
//        tabPrint(indent_level);
//        System.out.println("}");
    }

    @Override
    public void enterWhileStatement(MiniJavaParser.WhileStatementContext ctx) {
        tabPrint(indent_level);
        String str = "while " + ctx.LP() + " " + ctx.expression().getText() + " " + ctx.RP() ;
        if (! ctx.whileBlock().getText().startsWith("{")) {
            str = str.concat(" {");
            System.out.println(str);
        }else{
            nestedBlockForStatement = true;
            System.out.print(str);
        }

        indent_level += 1;
    }

    @Override
    public void exitWhileStatement(MiniJavaParser.WhileStatementContext ctx) {
        indent_level -= 1;
        if (! ctx.whileBlock().getText().startsWith("{")){
            tabPrint(indent_level);
            System.out.println("}");
        }
    }

    @Override
    public void enterPrintStatement(MiniJavaParser.PrintStatementContext ctx) {
        tabPrint(indent_level);
        String str = "System.out.println ( " + ctx.expression().getText() + " );" ;
        System.out.println(str);
    }

    @Override
    public void exitPrintStatement(MiniJavaParser.PrintStatementContext ctx) {

    }

    @Override
    public void enterVariableAssignmentStatement(MiniJavaParser.VariableAssignmentStatementContext ctx) {
        tabPrint(indent_level);
        String str = this.getExpression(ctx.expression().get(0)) + " = " + this.getExpression(ctx.expression().get(1)) + ";" ;
        System.out.println(str);
    }

    @Override
    public void exitVariableAssignmentStatement(MiniJavaParser.VariableAssignmentStatementContext ctx) {

    }

    @Override
    public void enterArrayAssignmentStatement(MiniJavaParser.ArrayAssignmentStatementContext ctx) {

    }

    @Override
    public void exitArrayAssignmentStatement(MiniJavaParser.ArrayAssignmentStatementContext ctx) {

    }

    @Override
    public void enterLocalVarDeclaration(MiniJavaParser.LocalVarDeclarationContext ctx) {

    }

    @Override
    public void exitLocalVarDeclaration(MiniJavaParser.LocalVarDeclarationContext ctx) {

    }

    @Override
    public void enterExpressioncall(MiniJavaParser.ExpressioncallContext ctx) {

    }

    @Override
    public void exitExpressioncall(MiniJavaParser.ExpressioncallContext ctx) {

    }

    @Override
    public void enterIfBlock(MiniJavaParser.IfBlockContext ctx) {
        if (! ctx.getText().startsWith("{")) {
            System.out.println("{");
        }else{
            nestedBlockForStatement = true;
        }
    }

    @Override
    public void exitIfBlock(MiniJavaParser.IfBlockContext ctx) {
        indent_level -= 1;
        if (! ctx.getText().endsWith("}")){
            tabPrint(indent_level);
            System.out.println("}");
        }
    }

    @Override
    public void enterElseBlock(MiniJavaParser.ElseBlockContext ctx) {
        tabPrint(indent_level);
        indent_level += 1;
        System.out.print("else");
        if (! ctx.getText().startsWith("{")) {
            System.out.println("{");
        }else{
            nestedBlockForStatement = true;
        }
    }

    @Override
    public void exitElseBlock(MiniJavaParser.ElseBlockContext ctx) {
        indent_level -= 1;
        if (! ctx.getText().endsWith("}")){
            tabPrint(indent_level);
            System.out.println("}");
        }
    }

    @Override
    public void enterWhileBlock(MiniJavaParser.WhileBlockContext ctx) {

    }

    @Override
    public void exitWhileBlock(MiniJavaParser.WhileBlockContext ctx) {

    }

    @Override
    public void enterLtExpression(MiniJavaParser.LtExpressionContext ctx) {

    }

    @Override
    public void exitLtExpression(MiniJavaParser.LtExpressionContext ctx) {

    }

    @Override
    public void enterObjectInstantiationExpression(MiniJavaParser.ObjectInstantiationExpressionContext ctx) {

    }

    @Override
    public void exitObjectInstantiationExpression(MiniJavaParser.ObjectInstantiationExpressionContext ctx) {

    }

    @Override
    public void enterArrayInstantiationExpression(MiniJavaParser.ArrayInstantiationExpressionContext ctx) {

    }

    @Override
    public void exitArrayInstantiationExpression(MiniJavaParser.ArrayInstantiationExpressionContext ctx) {

    }

    @Override
    public void enterPowExpression(MiniJavaParser.PowExpressionContext ctx) {

    }

    @Override
    public void exitPowExpression(MiniJavaParser.PowExpressionContext ctx) {

    }

    @Override
    public void enterIdentifierExpression(MiniJavaParser.IdentifierExpressionContext ctx) {

    }

    @Override
    public void exitIdentifierExpression(MiniJavaParser.IdentifierExpressionContext ctx) {

    }

    @Override
    public void enterMethodCallExpression(MiniJavaParser.MethodCallExpressionContext ctx) {

    }

    @Override
    public void exitMethodCallExpression(MiniJavaParser.MethodCallExpressionContext ctx) {

    }

    @Override
    public void enterNotExpression(MiniJavaParser.NotExpressionContext ctx) {

    }

    @Override
    public void exitNotExpression(MiniJavaParser.NotExpressionContext ctx) {

    }

    @Override
    public void enterBooleanLitExpression(MiniJavaParser.BooleanLitExpressionContext ctx) {

    }

    @Override
    public void exitBooleanLitExpression(MiniJavaParser.BooleanLitExpressionContext ctx) {

    }

    @Override
    public void enterParenExpression(MiniJavaParser.ParenExpressionContext ctx) {

    }

    @Override
    public void exitParenExpression(MiniJavaParser.ParenExpressionContext ctx) {

    }

    @Override
    public void enterIntLitExpression(MiniJavaParser.IntLitExpressionContext ctx) {

    }

    @Override
    public void exitIntLitExpression(MiniJavaParser.IntLitExpressionContext ctx) {

    }

    @Override
    public void enterStringLitExpression(MiniJavaParser.StringLitExpressionContext ctx) {

    }

    @Override
    public void exitStringLitExpression(MiniJavaParser.StringLitExpressionContext ctx) {

    }

    @Override
    public void enterNullLitExpression(MiniJavaParser.NullLitExpressionContext ctx) {

    }

    @Override
    public void exitNullLitExpression(MiniJavaParser.NullLitExpressionContext ctx) {

    }

    @Override
    public void enterAndExpression(MiniJavaParser.AndExpressionContext ctx) {

    }

    @Override
    public void exitAndExpression(MiniJavaParser.AndExpressionContext ctx) {

    }

    @Override
    public void enterArrayAccessExpression(MiniJavaParser.ArrayAccessExpressionContext ctx) {

    }

    @Override
    public void exitArrayAccessExpression(MiniJavaParser.ArrayAccessExpressionContext ctx) {

    }

    @Override
    public void enterAddExpression(MiniJavaParser.AddExpressionContext ctx) {

    }

    @Override
    public void exitAddExpression(MiniJavaParser.AddExpressionContext ctx) {

    }

    @Override
    public void enterThisExpression(MiniJavaParser.ThisExpressionContext ctx) {

    }

    @Override
    public void exitThisExpression(MiniJavaParser.ThisExpressionContext ctx) {

    }

    @Override
    public void enterFieldCallExpression(MiniJavaParser.FieldCallExpressionContext ctx) {

    }

    @Override
    public void exitFieldCallExpression(MiniJavaParser.FieldCallExpressionContext ctx) {

    }

    @Override
    public void enterArrayLengthExpression(MiniJavaParser.ArrayLengthExpressionContext ctx) {

    }

    @Override
    public void exitArrayLengthExpression(MiniJavaParser.ArrayLengthExpressionContext ctx) {

    }

    @Override
    public void enterIntarrayInstantiationExpression(MiniJavaParser.IntarrayInstantiationExpressionContext ctx) {

    }

    @Override
    public void exitIntarrayInstantiationExpression(MiniJavaParser.IntarrayInstantiationExpressionContext ctx) {

    }

    @Override
    public void enterSubExpression(MiniJavaParser.SubExpressionContext ctx) {

    }

    @Override
    public void exitSubExpression(MiniJavaParser.SubExpressionContext ctx) {

    }

    @Override
    public void enterMulExpression(MiniJavaParser.MulExpressionContext ctx) {

    }

    @Override
    public void exitMulExpression(MiniJavaParser.MulExpressionContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
