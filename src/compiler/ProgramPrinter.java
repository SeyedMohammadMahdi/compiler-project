package compiler;

import gen.MiniJavaListener;
import gen.MiniJavaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class ProgramPrinter implements MiniJavaListener {
    Stack<SymbolTable> currentScope;
    Queue<SymbolTable> scopes;
    int nested = 0;
    int id = 0;

    public ProgramPrinter() {
        this.currentScope = new Stack<SymbolTable>();
        this.scopes = new LinkedList<SymbolTable>();
    }

    private void printResult() {
        Iterator it = this.scopes.iterator();
        while (it.hasNext()){
            SymbolTable s = ((SymbolTable)it.next());
            s.print();
        }
    }

    @Override
    public void enterProgram(MiniJavaParser.ProgramContext ctx) {
        SymbolTable s = new SymbolTable("Program", id++, 0);
        this.currentScope.push(s);
        this.scopes.add(s);
    }

    @Override
    public void exitProgram(MiniJavaParser.ProgramContext ctx) {
        this.printResult();
    }

    @Override
    public void enterMainClass(MiniJavaParser.MainClassContext ctx) {
//        created this lines Symbol entry
        String key = "Key = MainClass_" + ctx.className.getText();
        String value = "Value = ";
        value += "MainClass: (name: " + ctx.className.getText() + ")";
        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);


//        created this scopes Symbol table
        String name = "MainClass_" + ctx.className.getText();
        int parentId =this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        SymbolTable table = new SymbolTable(name, id++, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);


    }

    @Override
    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        this.currentScope.pop();
    }

    @Override
    public void enterMainMethod(MiniJavaParser.MainMethodContext ctx) {
//        created this lines Symbol table entry
        String value = "Value = Method: (name: main) (returnType: void) (accessModifier: public) (parametersType: [array of [classType = String, isDefined = true] , index: 1] )";
        String key = "Key = method_main";
        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);
//        created this scopes Symbol table
        String name = "method_main";
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        SymbolTable table = new SymbolTable(name, id++, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
    }

    @Override
    public void exitMainMethod(MiniJavaParser.MainMethodContext ctx) {
        this.currentScope.pop();
    }

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
//        created this line's Symbol table entry
//        the variable i is for iterating over implemented Identifiers in class declaration
        int i = 1;
        String value = "Value = Class: (name: " + ctx.className.getText() + ")";
        if(ctx.inherits != null){
            value += " (extends: " + ctx.Identifier(i++).getText() + ")";
        }

        if(ctx.implements_ != null){
            value += " (implements: ";

            for (;i < ctx.Identifier().size(); i++){
                value += ctx.Identifier(i).getText();
                value += ", ";
            }
            value += ")";
        }
        String key = "Key = class_" + ctx.className.getText();
        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);

//        created this scopes Symbol table
        String name = "Class_" + ctx.className.getText();
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        SymbolTable table = new SymbolTable(name, id++, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
    }

    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        this.currentScope.pop();
    }

    @Override
    public void enterInterfaceDeclaration(MiniJavaParser.InterfaceDeclarationContext ctx) {
//        created this line's symbol table entry
        String value = "Value = interface: (name: " + ctx.Identifier().getText() + ")" ;
        String key = "Key = inteface_" + ctx.Identifier().getText();
        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);

//        created this scope's Symbol table
        String name = "interface_" + ctx.Identifier().getText();
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        SymbolTable table = new SymbolTable(name, id, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
    }

    @Override
    public void exitInterfaceDeclaration(MiniJavaParser.InterfaceDeclarationContext ctx) {
        this.currentScope.pop();
    }

    @Override
    public void enterInterfaceMethodDeclaration(MiniJavaParser.InterfaceMethodDeclarationContext ctx) {
//        created this line's symbol table entry
        String key = "Key = method_" + ctx.Identifier().getText();
        String value = "Value = Method: (name: " + ctx.Identifier().getText() + ") (returnType: " + ctx.returnType().getText() + ")";


        if (ctx.accessModifier() != null){
            value += " (accessModifier: " + ctx.accessModifier().getText() + ")";
        }

        if(ctx.parameterList() != null){
            int i = 0;
            int paramCount = ctx.parameterList().parameter().size();
            value += " (parametersType: ";
            for (;i < paramCount; i ++){
                if(ctx.parameterList().parameter(i).type().javaType() != null){
                    value += "[" + ctx.parameterList().parameter(i).type().getText() + ", " + "index: " + (i + 1 ) + "]";
                }
                else {
                    value += "[ classType:" + ctx.parameterList().parameter(i).type().Identifier().getText() + ", " + "index: " + (i + 1 ) + "]";
                }
            }
        }


        SymbolTableEntry entry  = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);

//        created symbol table for this scope
        String name = "interface_method_" + ctx.Identifier().getText();
        int parentId = this.currentScope.peek().id;
        int line =ctx.getStart().getLine();
        SymbolTable table = new SymbolTable(name, id, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);

    }

    @Override
    public void exitInterfaceMethodDeclaration(MiniJavaParser.InterfaceMethodDeclarationContext ctx) {
        this.currentScope.pop();
    }

    @Override
    public void enterFieldDeclaration(MiniJavaParser.FieldDeclarationContext ctx) {
//        created this line's Symbol table entry
        String key = "Key = var_" + ctx.Identifier().getText();
        String value = "Value = Field: (name: " + ctx.Identifier().getText() + ")";


        if(ctx.type().LSB() != null){
            value += " (type: array of " ;
        }
        else {
            value += " (type: ";
        }

        if(ctx.type().Identifier() != null){
            value += "[ classType: " + ctx.type().Identifier().getText() + " ])";
        }
        else {
            value += ctx.type().javaType().getText() + ")";
        }

        if(ctx.accessModifier() != null){
            value += " (accesModifier: " + ctx.accessModifier().getText() + ")";
        }

        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);
    }

    @Override
    public void exitFieldDeclaration(MiniJavaParser.FieldDeclarationContext ctx) {

    }

    @Override
    public void enterLocalDeclaration(MiniJavaParser.LocalDeclarationContext ctx) {
//        created this line's Symbol table entry
        String key = "Key = var_" + ctx.Identifier().getText();
        String value = "Value = LocalVar: (name: " + ctx.Identifier().getText() + ")";

        if(ctx.type().LSB() != null){
            value += " (type: array of ";
        }
        else {
            value += " (type: ";
        }

        if(ctx.type().javaType() != null){
            value += ctx.type().javaType().getText() + ")";
        }
        else {
            value += "[ classType: " + ctx.type().Identifier().getText() + " ])";
        }

        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);
    }

    @Override
    public void exitLocalDeclaration(MiniJavaParser.LocalDeclarationContext ctx) {

    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
//        created this line's Symbol table entry
        String key = "Key = mehtod_" + ctx.Identifier().getText();
        String value = "Value = Method: (name: " + ctx.Identifier().getText() + ") (returnType: " + ctx.returnType().getText() + ")";
        if(ctx.accessModifier() != null){
            value += " (accessModifier: " + ctx.accessModifier().getText();
        }

        if(ctx.parameterList() != null){
            int i = 0;
            int paramCount = ctx.parameterList().parameter().size();
            value += " (parametersType: ";
            for (;i < paramCount; i ++){
                if(ctx.parameterList().parameter(i).type().javaType() != null){
                    value += "[" + ctx.parameterList().parameter(i).type().getText() + ", " + "index: " + (i + 1 ) + "]";
                }
                else {
                    value += "[ classType:" + ctx.parameterList().parameter(i).type().Identifier().getText() + ", " + "index: " + (i + 1 ) + "]";
                }
            }
        }
        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);


//        created this scope's Symbol table
        String name = "method_" + ctx.Identifier().getText();
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        SymbolTable table = new SymbolTable(name, id, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        this.currentScope.pop();
    }

    @Override
    public void enterParameterList(MiniJavaParser.ParameterListContext ctx) {

    }

    @Override
    public void exitParameterList(MiniJavaParser.ParameterListContext ctx) {

    }

    @Override
    public void enterParameter(MiniJavaParser.ParameterContext ctx) {
//        created this line's Symbol Table entry
        String key = "Key = var_" + ctx.Identifier().getText();
        String value = "value = Parameter: (name: " + ctx.Identifier().getText() + ")";
        if(ctx.type().LSB() != null){
            value += " (type: array of ";
        }
        else {
            value += " (type: ";
        }

        if(ctx.type().javaType() != null){
            value += ctx.type().javaType().getText() + ")";
        }
        else {
            value += "[ classType: " + ctx.type().Identifier().getText() + "])";
        }

        SymbolTableEntry entry = new SymbolTableEntry(key, value);
        this.currentScope.peek().symbolTable.put(key, entry);
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
        this.nested++;
    }

    @Override
    public void exitNestedStatement(MiniJavaParser.NestedStatementContext ctx) {
        this.nested--;
    }

    @Override
    public void enterIfElseStatement(MiniJavaParser.IfElseStatementContext ctx) {


    }

    @Override
    public void exitIfElseStatement(MiniJavaParser.IfElseStatementContext ctx) {

    }

    @Override
    public void enterWhileStatement(MiniJavaParser.WhileStatementContext ctx) {

    }

    @Override
    public void exitWhileStatement(MiniJavaParser.WhileStatementContext ctx) {

    }

    @Override
    public void enterPrintStatement(MiniJavaParser.PrintStatementContext ctx) {

    }

    @Override
    public void exitPrintStatement(MiniJavaParser.PrintStatementContext ctx) {

    }

    @Override
    public void enterVariableAssignmentStatement(MiniJavaParser.VariableAssignmentStatementContext ctx) {

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
//        created this scopes Symbol table
        String name;
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        if(this.nested > 0){
            name = "nested_if";
        }
        else {
            name = "if";
        }

        SymbolTable table = new SymbolTable(name, id, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
        this.nested++;
    }

    @Override
    public void exitIfBlock(MiniJavaParser.IfBlockContext ctx) {
        this.currentScope.pop();
        this.nested--;
    }

    @Override
    public void enterElseBlock(MiniJavaParser.ElseBlockContext ctx) {
        String name;
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        if(this.nested > 0){
            name = "nested_else";
        }
        else {
            name = "else";
        }

        SymbolTable table = new SymbolTable(name, id, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
        this.nested++;
    }

    @Override
    public void exitElseBlock(MiniJavaParser.ElseBlockContext ctx) {
        this.currentScope.pop();
        this.nested--;
    }

    @Override
    public void enterWhileBlock(MiniJavaParser.WhileBlockContext ctx) {
        String name;
        int parentId = this.currentScope.peek().id;
        int line = ctx.getStart().getLine();
        if(this.nested > 0){
            name = "nested_while";
        }
        else {
            name = "while";
        }

        SymbolTable table = new SymbolTable(name, id, parentId, line);
        this.currentScope.push(table);
        this.scopes.add(table);
        this.nested++;
    }

    @Override
    public void exitWhileBlock(MiniJavaParser.WhileBlockContext ctx) {
        this.currentScope.pop();
        this.nested--;
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


class SymbolTable{
    public String name;
    public int id;
    public int parentId;
    public int line = 1;
    public Map<String, SymbolTableEntry> symbolTable;

    public SymbolTable(String name, int id, int parentId){
        this.symbolTable = new LinkedHashMap<>();
        this.name = name;
        this.id = id;
        this.parentId = parentId;
    }
    public SymbolTable(String name, int id, int parentId, int line){
        this.symbolTable = new LinkedHashMap<>();
        this.name = name;
        this.id = id;
        this.parentId = parentId;
        this.line = line;
    }

    public void print(){
        System.out.println("-------------- " + this.name + ": " + this.line + " --------------");
        if (!this.symbolTable.isEmpty()){
            for(Map.Entry<String, SymbolTableEntry> entry : this.symbolTable.entrySet()){
                entry.getValue().print();
            }
        }
        System.out.println("--------------------------------------------------------\n");

    }
}

class SymbolTableEntry{
    public String key;
    public String value;
    public SymbolTableEntry(String key, String value){
        this.key = key;
        this.value = value;
    }

    public void print(){
        System.out.print(key + "\t|\t");
        System.out.println(value);
    }
}