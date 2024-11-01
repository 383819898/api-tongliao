package com.shiku.im.db;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MappedClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.shiku.mianshi.Application;

import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AddressBook;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class MongoDBTest {
	
	@Autowired
	protected Morphia morphia;
	
	@Test
	public void testAddressDB() {
		SKBeanUtils.getAddressBookManger().notifyBook("8618316603690");
		while (true) {
			
		}
	}
	
	
	@Test
	public void testRoomFenBiao() {
		Integer userId=10004251;
		ObjectId id = new ObjectId();
		System.out.println(userId+"_"+id.getCounter());
	}
	@Test
	public void testDBObject() {
		DBObject dbObject=new BasicDBObject("aa",111);
		MongoCollection collection = SKBeanUtils.getLocalSpringBeanManager().getMongoClient().getDatabase("test1").getCollection("test",DBObject.class);
		collection.insertOne(dbObject);
	}
	
	
	@Test
	public void testDB() {
		
		System.out.println((10004541/1000)/10000);
		
		AddressBook book=new AddressBook();
		ObjectId id = new ObjectId();
		book.setId(id);
		book.setRegisterEd(1);
		book.setStatus(1);
		book.setTelephone("86183456123");
		book.setToTelephone("861584299254");
		MappedClass mappedClass = morphia.getMapper().getMappedClass(AddressBook.class);
		System.out.println("getCollectionName "+mappedClass.getCollectionName());
		DBObject dbObject = morphia.toDBObject(book);
		System.out.println(dbObject.toString());
		AddressBook fromDBObject = morphia.fromDBObject(SKBeanUtils.getDatastore(), AddressBook.class, dbObject);
		
		System.out.println(JSON.toJSONString(fromDBObject));
		System.out.println("id "+fromDBObject.getId().toString());
		
	}

}
