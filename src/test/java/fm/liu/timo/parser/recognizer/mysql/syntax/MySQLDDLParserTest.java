/*
 * Copyright 1999-2012 Alibaba Group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * (created at 2011-7-18)
 */
package fm.liu.timo.parser.recognizer.mysql.syntax;

import java.sql.SQLSyntaxErrorException;

import org.junit.Assert;

import fm.liu.timo.parser.ast.stmt.ddl.DDLCreateTableStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLStatement;
import fm.liu.timo.parser.ast.stmt.ddl.DDLTruncateStatement;
import fm.liu.timo.parser.recognizer.SQLParserDelegate;
import fm.liu.timo.parser.recognizer.mysql.MySQLToken;
import fm.liu.timo.parser.recognizer.mysql.lexer.MySQLLexer;
import fm.liu.timo.parser.visitor.OutputVisitor;

/**
 * @author <a href="mailto:danping.yudp@alibaba-inc.com">YU Danping</a>
 */
public class MySQLDDLParserTest extends AbstractSyntaxTest {

    public void testCreate() throws SQLSyntaxErrorException {
        String sql =
                "CREATE TABLE `stock` (\n" + "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,\n"
                        + "  `productid` bigint(20) unsigned NOT NULL,\n"
                        + "  `storeid` bigint(20) unsigned NOT NULL,\n"
                        + "  `quantity` int(10) unsigned NOT NULL DEFAULT '1',\n"
                        + "  PRIMARY KEY (`productid`,`storeid`),\n" + "  KEY `id` (`id`)\n"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        DDLCreateTableStatement stmt = (DDLCreateTableStatement) SQLParserDelegate.parse(sql);
        OutputVisitor visitor = new OutputVisitor(new StringBuilder());
        stmt.accept(visitor);
        System.out.println(visitor.getSql());
        sql = "CREATE TABLE IF NOT EXISTS `schema`.`Employee` (\n"
                + "`idEmployee` VARCHAR(45) NOT NULL ,\n" + "`Name` VARCHAR(255) NULL ,\n"
                + "`idAddresses` VARCHAR(45) NULL ,\n" + "PRIMARY KEY (`idEmployee`) ,\n"
                + "CONSTRAINT `fkEmployee_Addresses`\n"
                + "FOREIGN KEY `fkEmployee_Addresses` (`idAddresses`)\n"
                + "REFERENCES `schema`.`Addresses` (`idAddresses`)\n" + "ON DELETE NO ACTION\n"
                + "ON UPDATE NO ACTION)\n" + "ENGINE = InnoDB\n" + "DEFAULT CHARACTER SET = utf8\n"
                + "COLLATE = utf8_bin";
        stmt = (DDLCreateTableStatement) SQLParserDelegate.parse(sql);
        visitor = new OutputVisitor(new StringBuilder());
        stmt.accept(visitor);
        System.out.println(visitor.getSql());
    }

    public void testTruncate() throws Exception {
        String sql = "Truncate table tb1";
        MySQLLexer lexer = new MySQLLexer(sql);
        MySQLDDLParser parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        DDLStatement trun = (DDLTruncateStatement) parser.truncate();
        parser.match(MySQLToken.EOF);
        String output = output2MySQL(trun, sql);
        Assert.assertEquals("TRUNCATE TABLE tb1", output);

        sql = "Truncate tb1";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        trun = (DDLTruncateStatement) parser.truncate();
        parser.match(MySQLToken.EOF);
        output = output2MySQL(trun, sql);
        Assert.assertEquals("TRUNCATE TABLE tb1", output);
    }

    public void testDDLStmt() throws Exception {
        String sql = "alTer ignore table tb_name";
        MySQLLexer lexer = new MySQLLexer(sql);
        MySQLDDLParser parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        DDLStatement dst = parser.ddlStmt();

        sql = "alTeR table tb_name";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate temporary tabLe if not exists tb_name(id int)";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate tabLe if not exists tb_name(id serial)";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate temporary tabLe tb_name(id varchar(200))";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate unique index index_name on tb(col(id)) desc";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate fulltext index index_name on tb(col(id))";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate spatial index index_name on tb(col(id))";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "crEate index index_name using hash on tb(col(id))";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();

        sql = "drop index index_name on tb1";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        String output = output2MySQL(dst, sql);
        Assert.assertEquals("DROP INDEX index_name ON tb1", output);

        sql = "drop temporary tabLe if exists tb1,tb2,tb3 restrict";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 RESTRICT", output);

        sql = "drop temporary tabLe if exists tb1,tb2,tb3 cascade";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 CASCADE", output);

        sql = "drop temporary tabLe if exists tb1 cascade";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1 CASCADE", output);

        sql = "drop tabLe if exists tb1 cascade";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("DROP TABLE IF EXISTS tb1 CASCADE", output);

        sql = "drop temporary tabLe tb1 cascade";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("DROP TEMPORARY TABLE tb1 CASCADE", output);

        sql = "rename table tb1 to ntb1,tb2 to ntb2";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("RENAME TABLE tb1 TO ntb1, tb2 TO ntb2", output);

        sql = "rename table tb1 to ntb1";
        lexer = new MySQLLexer(sql);
        parser = new MySQLDDLParser(lexer, new MySQLExprParser(lexer));
        dst = parser.ddlStmt();
        output = output2MySQL(dst, sql);
        Assert.assertEquals("RENAME TABLE tb1 TO ntb1", output);
    }
}
