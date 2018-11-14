package fm.liu.timo.parser.visitor;

import java.util.Collection;

import fm.liu.timo.parser.ast.ASTNode;
import fm.liu.timo.parser.ast.expression.BinaryOperatorExpression;
import fm.liu.timo.parser.ast.expression.PolyadicOperatorExpression;
import fm.liu.timo.parser.ast.expression.UnaryOperatorExpression;
import fm.liu.timo.parser.ast.expression.comparison.BetweenAndExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionEqualsExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionGreaterThanExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionGreaterThanOrEqualsExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionIsExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionLessOrGreaterThanExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionLessThanExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionLessThanOrEqualsExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionNotEqualsExpression;
import fm.liu.timo.parser.ast.expression.comparison.ComparisionNullSafeEqualsExpression;
import fm.liu.timo.parser.ast.expression.comparison.InExpression;
import fm.liu.timo.parser.ast.expression.logical.LogicalAndExpression;
import fm.liu.timo.parser.ast.expression.logical.LogicalOrExpression;
import fm.liu.timo.parser.ast.expression.logical.LogicalXORExpression;
import fm.liu.timo.parser.ast.expression.misc.InExpressionList;
import fm.liu.timo.parser.ast.expression.misc.UserExpression;
import fm.liu.timo.parser.ast.expression.primary.CaseWhenOperatorExpression;
import fm.liu.timo.parser.ast.expression.primary.DefaultValue;
import fm.liu.timo.parser.ast.expression.primary.ExistsPrimary;
import fm.liu.timo.parser.ast.expression.primary.Identifier;
import fm.liu.timo.parser.ast.expression.primary.MatchExpression;
import fm.liu.timo.parser.ast.expression.primary.NewRowPrimary;
import fm.liu.timo.parser.ast.expression.primary.OldRowPrimary;
import fm.liu.timo.parser.ast.expression.primary.ParamMarker;
import fm.liu.timo.parser.ast.expression.primary.PlaceHolder;
import fm.liu.timo.parser.ast.expression.primary.RowExpression;
import fm.liu.timo.parser.ast.expression.primary.SysVarPrimary;
import fm.liu.timo.parser.ast.expression.primary.UsrDefVarPrimary;
import fm.liu.timo.parser.ast.expression.primary.function.FunctionExpression;
import fm.liu.timo.parser.ast.expression.primary.function.cast.Cast;
import fm.liu.timo.parser.ast.expression.primary.function.cast.Convert;
import fm.liu.timo.parser.ast.expression.primary.function.datetime.Extract;
import fm.liu.timo.parser.ast.expression.primary.function.datetime.GetFormat;
import fm.liu.timo.parser.ast.expression.primary.function.datetime.Timestampadd;
import fm.liu.timo.parser.ast.expression.primary.function.datetime.Timestampdiff;
import fm.liu.timo.parser.ast.expression.primary.function.flowctrl.Ifnull;
import fm.liu.timo.parser.ast.expression.primary.function.groupby.Avg;
import fm.liu.timo.parser.ast.expression.primary.function.groupby.Count;
import fm.liu.timo.parser.ast.expression.primary.function.groupby.GroupConcat;
import fm.liu.timo.parser.ast.expression.primary.function.groupby.Max;
import fm.liu.timo.parser.ast.expression.primary.function.groupby.Min;
import fm.liu.timo.parser.ast.expression.primary.function.groupby.Sum;
import fm.liu.timo.parser.ast.expression.primary.function.info.LastInsertId;
import fm.liu.timo.parser.ast.expression.primary.function.string.Char;
import fm.liu.timo.parser.ast.expression.primary.function.string.Trim;
import fm.liu.timo.parser.ast.expression.primary.literal.IntervalPrimary;
import fm.liu.timo.parser.ast.expression.primary.literal.LiteralBitField;
import fm.liu.timo.parser.ast.expression.primary.literal.LiteralBoolean;
import fm.liu.timo.parser.ast.expression.primary.literal.LiteralHexadecimal;
import fm.liu.timo.parser.ast.expression.primary.literal.LiteralNull;
import fm.liu.timo.parser.ast.expression.primary.literal.LiteralNumber;
import fm.liu.timo.parser.ast.expression.primary.literal.LiteralString;
import fm.liu.timo.parser.ast.expression.string.LikeExpression;
import fm.liu.timo.parser.ast.expression.type.CollateExpression;
import fm.liu.timo.parser.ast.fragment.GroupBy;
import fm.liu.timo.parser.ast.fragment.Limit;
import fm.liu.timo.parser.ast.fragment.OrderBy;
import fm.liu.timo.parser.ast.fragment.ddl.ColumnDefinition;
import fm.liu.timo.parser.ast.fragment.ddl.TableOptions;
import fm.liu.timo.parser.ast.fragment.ddl.datatype.DataType;
import fm.liu.timo.parser.ast.fragment.ddl.index.IndexColumnName;
import fm.liu.timo.parser.ast.fragment.ddl.index.IndexDefinition;
import fm.liu.timo.parser.ast.fragment.ddl.index.IndexOption;
import fm.liu.timo.parser.ast.fragment.tableref.Dual;
import fm.liu.timo.parser.ast.fragment.tableref.IndexHint;
import fm.liu.timo.parser.ast.fragment.tableref.InnerJoin;
import fm.liu.timo.parser.ast.fragment.tableref.NaturalJoin;
import fm.liu.timo.parser.ast.fragment.tableref.OuterJoin;
import fm.liu.timo.parser.ast.fragment.tableref.StraightJoin;
import fm.liu.timo.parser.ast.fragment.tableref.SubqueryFactor;
import fm.liu.timo.parser.ast.fragment.tableref.TableRefFactor;
import fm.liu.timo.parser.ast.fragment.tableref.TableReferences;
import fm.liu.timo.parser.ast.stmt.compound.BeginEndStatement;
import fm.liu.timo.parser.ast.stmt.compound.DeclareStatement;
import fm.liu.timo.parser.ast.stmt.compound.condition.DeclareConditionStatement;
import fm.liu.timo.parser.ast.stmt.compound.condition.DeclareHandlerStatement;
import fm.liu.timo.parser.ast.stmt.compound.condition.GetDiagnosticsStatement;
import fm.liu.timo.parser.ast.stmt.compound.condition.ResignalStatement;
import fm.liu.timo.parser.ast.stmt.compound.condition.SignalStatement;
import fm.liu.timo.parser.ast.stmt.compound.cursors.CursorCloseStatement;
import fm.liu.timo.parser.ast.stmt.compound.cursors.CursorDeclareStatement;
import fm.liu.timo.parser.ast.stmt.compound.cursors.CursorFetchStatement;
import fm.liu.timo.parser.ast.stmt.compound.cursors.CursorOpenStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.CaseStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.IfStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.IterateStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.LeaveStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.LoopStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.RepeatStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.ReturnStatement;
import fm.liu.timo.parser.ast.stmt.compound.flowcontrol.WhileStatement;
import fm.liu.timo.parser.ast.stmt.dal.DALSetCharacterSetStatement;
import fm.liu.timo.parser.ast.stmt.dal.DALSetNamesStatement;
import fm.liu.timo.parser.ast.stmt.dal.DALSetStatement;
import fm.liu.timo.parser.ast.stmt.dal.ShowAuthors;
import fm.liu.timo.parser.ast.stmt.dal.ShowBinLogEvent;
import fm.liu.timo.parser.ast.stmt.dal.ShowBinaryLog;
import fm.liu.timo.parser.ast.stmt.dal.ShowCharaterSet;
import fm.liu.timo.parser.ast.stmt.dal.ShowCharset;
import fm.liu.timo.parser.ast.stmt.dal.ShowCollation;
import fm.liu.timo.parser.ast.stmt.dal.ShowColumns;
import fm.liu.timo.parser.ast.stmt.dal.ShowContributors;
import fm.liu.timo.parser.ast.stmt.dal.ShowCreate;
import fm.liu.timo.parser.ast.stmt.dal.ShowCreateDatabase;
import fm.liu.timo.parser.ast.stmt.dal.ShowDatabases;
import fm.liu.timo.parser.ast.stmt.dal.ShowEngine;
import fm.liu.timo.parser.ast.stmt.dal.ShowEngines;
import fm.liu.timo.parser.ast.stmt.dal.ShowErrors;
import fm.liu.timo.parser.ast.stmt.dal.ShowEvents;
import fm.liu.timo.parser.ast.stmt.dal.ShowFields;
import fm.liu.timo.parser.ast.stmt.dal.ShowFunctionCode;
import fm.liu.timo.parser.ast.stmt.dal.ShowFunctionStatus;
import fm.liu.timo.parser.ast.stmt.dal.ShowGrants;
import fm.liu.timo.parser.ast.stmt.dal.ShowIndex;
import fm.liu.timo.parser.ast.stmt.dal.ShowMasterStatus;
import fm.liu.timo.parser.ast.stmt.dal.ShowOpenTables;
import fm.liu.timo.parser.ast.stmt.dal.ShowPlugins;
import fm.liu.timo.parser.ast.stmt.dal.ShowPrivileges;
import fm.liu.timo.parser.ast.stmt.dal.ShowProcedureCode;
import fm.liu.timo.parser.ast.stmt.dal.ShowProcedureStatus;
import fm.liu.timo.parser.ast.stmt.dal.ShowProcesslist;
import fm.liu.timo.parser.ast.stmt.dal.ShowProfile;
import fm.liu.timo.parser.ast.stmt.dal.ShowProfiles;
import fm.liu.timo.parser.ast.stmt.dal.ShowSlaveHosts;
import fm.liu.timo.parser.ast.stmt.dal.ShowSlaveStatus;
import fm.liu.timo.parser.ast.stmt.dal.ShowStatus;
import fm.liu.timo.parser.ast.stmt.dal.ShowTableStatus;
import fm.liu.timo.parser.ast.stmt.dal.ShowTables;
import fm.liu.timo.parser.ast.stmt.dal.ShowTriggers;
import fm.liu.timo.parser.ast.stmt.dal.ShowVariables;
import fm.liu.timo.parser.ast.stmt.dal.ShowWarnings;
import fm.liu.timo.parser.ast.stmt.ddl.DDLAlterEventStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLAlterTableStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLAlterTableStatement.AlterSpecification;
import fm.liu.timo.parser.ast.stmt.ddl.DDLAlterTableStatement.WithValidation;
import fm.liu.timo.parser.ast.stmt.ddl.DDLAlterViewStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateEventStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateFunctionStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateIndexStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateLikeStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateProcedureStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateTableStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateTableStatement.ForeignKeyDefinition;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateTriggerStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateViewStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLDropIndexStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLDropTableStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLDropTriggerStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLRenameTableStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLTruncateStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DescTableStatement;
import fm.liu.timo.parser.ast.stmt.ddl.ExplainStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLCallStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLDeleteStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLInsertStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLReplaceStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLSelectStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLSelectUnionStatement;
import fm.liu.timo.parser.ast.stmt.dml.DMLUpdateStatement;
import fm.liu.timo.parser.ast.stmt.extension.ExtDDLCreatePolicy;
import fm.liu.timo.parser.ast.stmt.extension.ExtDDLDropPolicy;
import fm.liu.timo.parser.ast.stmt.mts.MTSCommitStatement;
import fm.liu.timo.parser.ast.stmt.mts.MTSReleaseStatement;
import fm.liu.timo.parser.ast.stmt.mts.MTSRollbackStatement;
import fm.liu.timo.parser.ast.stmt.mts.MTSSavepointStatement;
import fm.liu.timo.parser.ast.stmt.mts.MTSSetTransactionStatement;
import fm.liu.timo.parser.ast.stmt.mts.MTSStartTransactionStatement;
import fm.liu.timo.parser.util.Pair;

public abstract class Visitor {

    protected int stackDeep = 0;

    @SuppressWarnings({"rawtypes"})
    protected void visitChild(Object obj) {
        if (obj == null)
            return;
        stackDeep++;
        if (obj instanceof ASTNode) {
            ((ASTNode) obj).accept(this);
        } else if (obj instanceof Collection) {
            for (Object o : (Collection) obj) {
                visitChild(o);
            }
        } else if (obj instanceof Pair) {
            visitChild(((Pair) obj).getKey());
            visitChild(((Pair) obj).getValue());
        }
        stackDeep--;
    }

    public void visit(BetweenAndExpression node) {
        visitChild(node.getFirst());
        visitChild(node.getSecond());
        visitChild(node.getThird());
    }

    public void visit(ComparisionIsExpression node) {
        visitChild(node.getOperand());
    }

    public void visit(InExpressionList node) {
        visitChild(node.getList());
    }

    public void visit(LikeExpression node) {
        visitChild(node.getFirst());
        visitChild(node.getSecond());
        visitChild(node.getThird());
    }

    public void visit(CollateExpression node) {
        visitChild(node.getString());
    }

    public void visit(LogicalXORExpression node) {
        visitChild(node.getLeftOprand());
        visitChild(node.getRightOprand());
    }

    public void visit(UserExpression node) {}

    public void visit(UnaryOperatorExpression node) {
        visitChild(node.getOperand());
    }

    public void visit(BinaryOperatorExpression node) {
        visitChild(node.getLeftOprand());
        visitChild(node.getRightOprand());
    }

    public void visit(PolyadicOperatorExpression node) {
        for (int i = 0, len = node.getArity(); i < len; ++i) {
            visitChild(node.getOperand(i));
        }
    }

    public void visit(LogicalAndExpression node) {
        visit((PolyadicOperatorExpression) node);
    }

    public void visit(LogicalOrExpression node) {
        visit((PolyadicOperatorExpression) node);
    }

    public void visit(ComparisionEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    public void visit(ComparisionNotEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    public void visit(ComparisionLessOrGreaterThanExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    public void visit(ComparisionNullSafeEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    public void visit(ComparisionGreaterThanExpression node) {
        visitChild(node.getLeftOprand());
        visitChild(node.getRightOprand());
    }

    public void visit(ComparisionGreaterThanOrEqualsExpression node) {
        visitChild(node.getLeftOprand());
        visitChild(node.getRightOprand());
    }

    public void visit(ComparisionLessThanExpression node) {
        visitChild(node.getLeftOprand());
        visitChild(node.getRightOprand());
    }

    public void visit(ComparisionLessThanOrEqualsExpression node) {
        visitChild(node.getLeftOprand());
        visitChild(node.getRightOprand());
    }

    public void visit(InExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    public void visit(FunctionExpression node) {
        visitChild(node.getArguments());
    }

    public void visit(LastInsertId node) {
        visitChild(node.getArguments());
    }

    public void visit(Char node) {
        visit((FunctionExpression) node);
    }

    public void visit(Convert node) {
        visit((FunctionExpression) node);
    }

    public void visit(Trim node) {
        visit((FunctionExpression) node);
        visitChild(node.getRemainString());
        visitChild(node.getString());
    }

    public void visit(Cast node) {
        visit((FunctionExpression) node);
        visitChild(node.getExpr());
        visitChild(node.getTypeInfo1());
        visitChild(node.getTypeInfo2());
    }

    public void visit(Avg node) {
        visit((FunctionExpression) node);
    }

    public void visit(Max node) {
        visit((FunctionExpression) node);
    }

    public void visit(Min node) {
        visit((FunctionExpression) node);
    }

    public void visit(Sum node) {
        visit((FunctionExpression) node);
    }

    public void visit(Count node) {
        visit((FunctionExpression) node);
    }

    public void visit(GroupConcat node) {
        visit((FunctionExpression) node);
    }

    public void visit(Timestampdiff node) {
        visit((FunctionExpression) node);
    }

    public void visit(Timestampadd node) {
        visit((FunctionExpression) node);
    }

    public void visit(Extract node) {
        visit((FunctionExpression) node);
    }

    public void visit(GetFormat node) {
        visit((FunctionExpression) node);
    }

    public void visit(Ifnull node) {
        visit((FunctionExpression) node);
    }

    public void visit(IntervalPrimary node) {
        visitChild(node.getQuantity());
    }

    public void visit(LiteralBitField node) {}

    public void visit(LiteralBoolean node) {}

    public void visit(LiteralHexadecimal node) {}

    public void visit(LiteralNull node) {}

    public void visit(LiteralNumber node) {}

    public void visit(LiteralString node) {}

    public void visit(CaseWhenOperatorExpression node) {
        visitChild(node.getComparee());
        visitChild(node.getElseResult());
        visitChild(node.getWhenList());
    }

    public void visit(DefaultValue node) {}

    public void visit(ExistsPrimary node) {
        visitChild(node.getSubquery());
    }

    public void visit(PlaceHolder node) {}

    public void visit(Identifier node) {}

    public void visit(MatchExpression node) {
        visitChild(node.getColumns());
        visitChild(node.getPattern());
    }

    public void visit(ParamMarker node) {}

    public void visit(RowExpression node) {
        visitChild(node.getRowExprList());
    }

    public void visit(SysVarPrimary node) {}

    public void visit(UsrDefVarPrimary node) {}

    public void visit(IndexHint node) {}

    public void visit(InnerJoin node) {
        visitChild(node.getLeftTableRef());
        visitChild(node.getOnCond());
        visitChild(node.getRightTableRef());
    }

    public void visit(NaturalJoin node) {
        visitChild(node.getLeftTableRef());
        visitChild(node.getRightTableRef());
    }

    public void visit(OuterJoin node) {
        visitChild(node.getLeftTableRef());
        visitChild(node.getOnCond());
        visitChild(node.getRightTableRef());
    }

    public void visit(StraightJoin node) {
        visitChild(node.getLeftTableRef());
        visitChild(node.getOnCond());
        visitChild(node.getRightTableRef());
    }

    public void visit(SubqueryFactor node) {
        visitChild(node.getSubquery());
    }

    public void visit(TableReferences node) {
        visitChild(node.getTableReferenceList());
    }

    public void visit(TableRefFactor node) {
        visitChild(node.getHintList());
        visitChild(node.getTable());
        visitChild(node.getParamMarker());
    }

    public void visit(Dual dual) {}

    public void visit(GroupBy node) {
        visitChild(node.getOrderByList());
    }

    public void visit(Limit node) {
        visitChild(node.getOffset());
        visitChild(node.getSize());
    }

    public void visit(OrderBy node) {
        visitChild(node.getOrderByList());
    }

    public void visit(ColumnDefinition columnDefinition) {}

    public void visit(IndexOption indexOption) {}

    public void visit(IndexColumnName indexColumnName) {}

    public void visit(TableOptions node) {}

    public void visit(AlterSpecification node) {}

    public void visit(DataType node) {}

    public void visit(ShowAuthors node) {}

    public void visit(ShowBinaryLog node) {}

    public void visit(ShowBinLogEvent node) {
        visitChild(node.getLimit());
        visitChild(node.getPos());
    }

    public void visit(ShowCharaterSet node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowCharset node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowCollation node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowColumns node) {
        visitChild(node.getTable());
        visitChild(node.getWhere());
        visitChild(node.getPattern());
    }

    public void visit(ShowContributors node) {}

    public void visit(ShowCreate node) {
        visitChild(node.getId());
    }

    public void visit(ShowDatabases node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowEngine node) {}

    public void visit(ShowEngines node) {}

    public void visit(ShowErrors node) {
        visitChild(node.getLimit());
    }

    public void visit(ShowEvents node) {
        visitChild(node.getSchema());
        visitChild(node.getWhere());
    }

    public void visit(ShowFunctionCode node) {
        visitChild(node.getFunctionName());
    }

    public void visit(ShowFunctionStatus node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowGrants node) {
        visitChild(node.getUser());
    }

    public void visit(ShowIndex node) {
        visitChild(node.getTable());
    }

    public void visit(ShowMasterStatus node) {}

    public void visit(ShowOpenTables node) {
        visitChild(node.getSchema());
        visitChild(node.getWhere());
    }

    public void visit(ShowPlugins node) {}

    public void visit(ShowPrivileges node) {}

    public void visit(ShowProcedureCode node) {
        visitChild(node.getProcedureName());
    }

    public void visit(ShowProcedureStatus node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowProcesslist node) {}

    public void visit(ShowProfile node) {
        visitChild(node.getForQuery());
        visitChild(node.getLimit());
    }

    public void visit(ShowProfiles node) {}

    public void visit(ShowSlaveHosts node) {}

    public void visit(ShowSlaveStatus node) {}

    public void visit(ShowStatus node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowTables node) {
        visitChild(node.getSchema());
        visitChild(node.getWhere());
    }

    public void visit(ShowTableStatus node) {
        visitChild(node.getDatabase());
        visitChild(node.getWhere());
    }

    public void visit(ShowTriggers node) {
        visitChild(node.getSchema());
        visitChild(node.getWhere());
    }

    public void visit(ShowVariables node) {
        visitChild(node.getWhere());
    }

    public void visit(ShowWarnings node) {
        visitChild(node.getLimit());
    }

    public void visit(DescTableStatement node) {
        visitChild(node.getTable());
    }

    public void visit(DALSetStatement node) {
        visitChild(node.getAssignmentList());
    }

    public void visit(DALSetNamesStatement node) {}

    public void visit(DALSetCharacterSetStatement node) {}

    public void visit(DMLCallStatement node) {
        visitChild(node.getArguments());
        visitChild(node.getProcedure());
    }

    public void visit(DMLDeleteStatement node) {
        visitChild(node.getLimit());
        visitChild(node.getOrderBy());
        visitChild(node.getTableNames());
        visitChild(node.getTableRefs());
        visitChild(node.getWhereCondition());
    }

    public void visit(DMLInsertStatement node) {
        visitChild(node.getColumnNameList());
        visitChild(node.getDuplicateUpdate());
        visitChild(node.getRowList());
        visitChild(node.getSelect());
        visitChild(node.getTable());
    }

    public void visit(DMLReplaceStatement node) {
        visitChild(node.getColumnNameList());
        visitChild(node.getRowList());
        visitChild(node.getSelect());
        visitChild(node.getTable());
    }

    public void visit(DMLSelectStatement node) {
        visitChild(node.getGroup());
        visitChild(node.getHaving());
        visitChild(node.getLimit());
        visitChild(node.getOrder());
        stackDeep = 0;
        visitChild(node.getSelectExprList());
        visitChild(node.getTables());
        visitChild(node.getWhere());
    }

    public void visit(DMLSelectUnionStatement node) {
        visitChild(node.getLimit());
        visitChild(node.getOrderBy());
        stackDeep = 0;
        visitChild(node.getSelectStmtList());
    }

    public void visit(DMLUpdateStatement node) {
        visitChild(node.getLimit());
        visitChild(node.getOrderBy());
        visitChild(node.getTableRefs());
        visitChild(node.getValues());
        visitChild(node.getWhere());
    }

    public void visit(MTSSetTransactionStatement node) {}

    public void visit(MTSSavepointStatement node) {
        visitChild(node.getSavepoint());
    }

    public void visit(MTSReleaseStatement node) {
        visitChild(node.getSavepoint());
    }

    public void visit(MTSRollbackStatement node) {
        visitChild(node.getSavepoint());
    }

    public void visit(MTSCommitStatement node) {}

    public void visit(DDLTruncateStatement node) {
        visitChild(node.getTable());
    }

    public void visit(DDLAlterTableStatement node) {
        visitChild(node.getTable());
    }

    public void visit(DDLCreateIndexStatement node) {
        visitChild(node.getIndexDefinition());
        visitChild(node.getTable());
    }

    public void visit(DDLCreateTableStatement node) {
        visitChild(node.getTable());
    }

    /**
     *
     * @param node | CREATE TABLE tbl_name { LIKE old_tbl_name | (LIKE old_tbl_name) }
     */
    public void visit(DDLCreateLikeStatement node) {
        visitChild(node.getTable());
    }

    public void visit(DDLRenameTableStatement node) {
        visitChild(node.getList());
    }

    public void visit(DDLDropIndexStatement node) {
        visitChild(node.getIndexName());
        visitChild(node.getTable());
    }

    public void visit(DDLDropTableStatement node) {
        visitChild(node.getTableNames());
    }

    public void visit(ExtDDLCreatePolicy node) {}

    public void visit(ExtDDLDropPolicy node) {}

    public void visit(ShowFields node) {
        visitChild(node.getTable());
        visitChild(node.getPattern());
        visitChild(node.getWhere());
    }

    public void visit(DDLAlterViewStatement node) {}

    public void visit(DDLAlterEventStatement node) {}

    public void visit(DDLCreateEventStatement node) {}

    public void visit(DDLCreateTriggerStatement node) {
        visitChild(node.getDefiner());
        visitChild(node.getTriggerName());
        visitChild(node.getTriggerTime());
        visitChild(node.getTriggerEvent());
        visitChild(node.getTable());
        visitChild(node.getTriggerOrder());
        visitChild(node.getOtherTriggerName());
        visitChild(node.getStmt());
    }

    public void visit(DDLCreateViewStatement node) {}

    public void visit(MTSStartTransactionStatement mtsStartTransactionStatement) {};

    public void visit(ShowCreateDatabase node) {
        visitChild(node.getDbName());
    }

    public void visit(ExplainStatement node) {
        visitChild(node.getTblName());
        visitChild(node.getExplainableStmt());
    }

    public void visit(ForeignKeyDefinition foreignKeyDefinition) {}

    public void visit(WithValidation withValidation) {}

    public void visit(IfStatement node) {
        visitChild(node.getIfStatements());
        visitChild(node.getElseStatement());
    }

    public void visit(BeginEndStatement node) {
        visitChild(node.getLabel());
        visitChild(node.getStatements());
    }

    public void visit(NewRowPrimary node) {}

    public void visit(OldRowPrimary node) {}

    public void visit(LoopStatement node) {
        visitChild(node.getLabel());
        visitChild(node.getStmt());
    }

    public void visit(IterateStatement node) {
        visitChild(node.getLabel());
    }

    public void visit(LeaveStatement node) {
        visitChild(node.getLabel());
    }

    public void visit(ReturnStatement node) {
        visitChild(node.getLabel());
    }

    public void visit(RepeatStatement node) {
        visitChild(node.getLabel());
        visitChild(node.getStmt());
        visitChild(node.getUtilCondition());
    }

    public void visit(WhileStatement node) {
        visitChild(node.getLabel());
        visitChild(node.getStmt());
        visitChild(node.getWhileCondition());
    }

    public void visit(CaseStatement node) {
        visitChild(node.getCaseValue());
        visitChild(node.getWhenList());
        visitChild(node.getElseStmt());
    }

    public void visit(DeclareStatement node) {
        visitChild(node.getVarNames());
        visitChild(node.getDataType());
    }

    public void visit(DeclareHandlerStatement node) {
        visitChild(node.getStmt());
    }

    public void visit(DeclareConditionStatement node) {
        visitChild(node.getName());
    }

    public void visit(CursorDeclareStatement node) {
        visitChild(node.getName());
        visitChild(node.getStmt());
    }

    public void visit(CursorCloseStatement node) {
        visitChild(node.getName());
    }

    public void visit(CursorOpenStatement node) {
        visitChild(node.getName());
    }

    public void visit(CursorFetchStatement node) {
        visitChild(node.getName());
        visitChild(node.getVarNames());
    }

    public void visit(SignalStatement node) {
        visitChild(node.getInformationItems());
    }

    public void visit(ResignalStatement node) {
        visitChild(node.getInformationItems());
    }

    public void visit(GetDiagnosticsStatement node) {
        visitChild(node.getStatementItems());
        visitChild(node.getConditionItems());
    }

    public void visit(DDLCreateProcedureStatement node) {
        visitChild(node.getDefiner());
        visitChild(node.getName());
        visitChild(node.getParameters());
        visitChild(node.getCharacteristics());
        visitChild(node.getStmt());
    }

    public void visit(DDLCreateFunctionStatement node) {
        visitChild(node.getDefiner());
        visitChild(node.getName());
        visitChild(node.getParameters());
        visitChild(node.getReturns());
        visitChild(node.getCharacteristics());
        visitChild(node.getStmt());
    }

    public void visit(DDLDropTriggerStatement node) {
        visitChild(node.getName());
    }

    public void visit(IndexDefinition node) {}

}
