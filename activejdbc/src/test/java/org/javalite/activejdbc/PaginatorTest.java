/*
Copyright 2009-2010 Igor Polevoy 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/


package org.javalite.activejdbc;

import org.javalite.activejdbc.test.ActiveJDBCTest;
import org.javalite.activejdbc.test_models.Item;
import org.junit.Test;

import java.io.*;
import java.util.List;


/**
 * @author Igor Polevoy
 */
public class PaginatorTest extends ActiveJDBCTest {

    @Override
    public void before() throws Exception {
        super.before();
        deleteAndPopulateTable("items");
        for(int i = 1; i <= 1000; i++){
            Item item = (Item)Item.create("item_number", i, "item_description", "this is item # " + i);
            item.saveIt();
        }
    }

    @Test
    public void testPageCount(){
        Paginator p = new Paginator(Item.class, 10, "item_description like '%2%'");
        a(p.pageCount()).shouldBeEqual(28);
    }

    @Test
    public void testGetPage(){
        Paginator p = new Paginator(Item.class, 10, "item_description like ?", "%2%").orderBy("item_number");
        List<Item> items = p.getPage(28);
        a(items.size()).shouldBeEqual(1);
        a(items.get(0).get("item_number")).shouldBeEqual(992);


        final Paginator p1 = new Paginator(Item.class, 10, "*").orderBy("item_number");
        items = p1.getPage(2);
        a(items.size()).shouldBeEqual(10);
        a(items.get(0).get("item_number")).shouldBeEqual(11);//page start
        a(items.get(9).get("item_number")).shouldBeEqual(20);//page end
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionIfWrongArgument(){
        final Paginator p1 = new Paginator(Item.class, 10, "*").orderBy("item_number");
        p1.getPage(-2);
    }

    @Test
    public void testThatPaginatorIsSerializable() throws IOException, ClassNotFoundException {

        Paginator p = new Paginator(Item.class, 10, "*").orderBy("item_number");
        //serialize:
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout  = new ObjectOutputStream(bout);
        oout.writeObject(p);
        oout.flush();

        //De-serialize:
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream oin = new ObjectInputStream(bin);
        Paginator p1 = (Paginator)oin.readObject();
        a(p1.pageCount()).shouldBeEqual(100);
    }

    @Test
    public void testPreviousAndNext(){
        //for this query we have only 28 pages
        Paginator p = new Paginator(Item.class, 10, "item_description like '%2%'");

        p.getPage(27);
        a(p.hasNext()).shouldBeTrue();

        p.getPage(28);
        a(p.hasNext()).shouldBeFalse();
        a(p.hasPrevious()).shouldBeTrue();

        p.getPage(2);
        a(p.hasPrevious()).shouldBeTrue();

        p.getPage(1);
        a(p.hasPrevious()).shouldBeFalse();
        a(p.hasNext()).shouldBeTrue();


        p = new Paginator(Item.class, 10, "*").orderBy("item_number");
        a(p.<Model>getPage(1).size()).shouldBeEqual(10);

        LazyList<Model> page2 = p.getPage(2);
        a(page2.get(0).get("item_number")).shouldBeEqual(11);

        System.out.println("Page 1");
        p.getPage(1).dump(System.out);
        System.out.println("Page 2");
        p.getPage(2).dump(System.out);
        System.out.println("Page 3");
        p.getPage(3).dump(System.out);

    }

    @Test
    public void shouldPaginateWithRawSql(){

        Paginator p = new Paginator(Item.class, 10, "select * from items where item_description like '%2%'").orderBy("item_number");
        List<Item> items = p.getPage(28);
        a(items.size()).shouldBeEqual(1);
        a(items.get(0).get("item_number")).shouldBeEqual(992);
        a(p.pageCount()).shouldBeEqual(28);
    }
}
