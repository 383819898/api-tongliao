package com.shiku.im.mainTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Meta;
import org.mongodb.morphia.query.Meta.MetaDataKeyword;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.shiku.im.model.TigaseUser;

import cn.xyz.commons.autoconfigure.KMongoAutoConfiguration;
import cn.xyz.mianshi.model.RoomVO;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.Emoji;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.Room.Member;

public class MainTest {

	public static void main(String[] args)
			throws InterruptedException, ScriptException, NoSuchMethodException, IOException {

		Morphia morphia = new Morphia();
		morphia.mapPackage("cn.xyz.mianshi.vo"); //

		// MongoClient mongoClient = new MongoClient(new
		// MongoClientURI("mongodb://110.173.52.154:23451")); //
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));

		Datastore datastore = morphia.createDatastore(mongoClient, "imapi");

		Query<Emoji> emoji = datastore.createQuery(Emoji.class);
		List<Emoji> list = emoji.asList();

		for (int i = 0; i < list.size(); i++) { // 新缘 hongxingcl->maixia123.com
			
			Query<Emoji> query = datastore.createQuery(Emoji.class).filter("_id", list.get(i).getEmojiId());
			
			
			if (list.get(i).getUrl() != null) {
				if (list.get(i).getUrl().contains("hongxingcl")) {
					list.get(i).setUrl(list.get(i).getUrl().replace("hongxingcl", "maixia123"));

				}
			}

			if (list.get(i).getMsg() != null) {
				if (list.get(i).getMsg().contains("hongxingcl")) {
					list.get(i).setMsg(list.get(i).getMsg().replace("hongxingcl", "maixia123"));
				}
			}

			if (list.get(i).getUrl() != null && list.get(i).getMsg() != null) {
				UpdateOperations<Emoji> updateOperations = datastore.createUpdateOperations(Emoji.class)
						.set("url", list.get(i).getUrl()).set("msg", list.get(i).getMsg());
				UpdateResults result = datastore.update(query, updateOperations);
				System.out.println(result);
			}
			if (list.get(i).getUrl() != null && list.get(i).getMsg() == null) {
				UpdateOperations<Emoji> updateOperations = datastore.createUpdateOperations(Emoji.class).set("url",
						list.get(i).getUrl());
				UpdateResults result = datastore.update(query, updateOperations);
				System.out.println(result);
			}
			if (list.get(i).getUrl() == null && list.get(i).getMsg() != null) {
				UpdateOperations<Emoji> updateOperations = datastore.createUpdateOperations(Emoji.class).set("msg",
						list.get(i).getMsg());
				UpdateResults result = datastore.update(query, updateOperations);
				System.out.println(result);
			}
			System.out.println("i =" + i);

		}
		mongoClient.close();

		Morphia morphia2 = new Morphia();
		morphia2.mapPackage("com.shiku.im.model");
		// entity所在包路径

		/*MongoClient mongoClient2 = new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017")); //
		//MongoClient mongoClient2 = new MongoClient(new MongoClientURI("mongodb://110.173.52.154:23451"));
		Datastore datastore2 = morphia2.createDatastore(mongoClient2, "tigase");
		System.out.println("---" + datastore2);

		Query<TigaseUser> users = datastore2.createQuery(TigaseUser.class);
		System.out.println(users.asList().size());

		List<TigaseUser> userList = users.asList();

		for (int i = 0; i < userList.size(); i++) { // 新缘 hongxingcl->maixia123.com

			String userId = userList.get(i).getUser_id();

			System.out.println("userList.get(i).getUser_id()==== " + userList.get(i).getUser_id());

			Query<TigaseUser> query = datastore2.createQuery(TigaseUser.class).filter("user_id", userId);
			System.out.println("query  :" + query);
			System.out.println("query result =  :" + query.asList().get(0).getUser_id());

			if (userList.get(i).getDomain().equals("im.hongxingcl.com")) {
				userList.get(i).setDomain("im.maixia123.com");
			}
			if (userList.get(i).getUser_id().contains("hongxingcl")) {
				userList.get(i).setUser_id(userList.get(i).getUser_id().replace("hongxingcl", "maixia123"));
			}

			UpdateOperations<TigaseUser> updateOperations = datastore2.createUpdateOperations(TigaseUser.class)
					.set("domain", userList.get(i).getDomain()).set("user_id", userList.get(i).getUser_id());
			UpdateResults result = datastore2.update(query, updateOperations);
			System.out.println(result);

			System.out.println(i);
		}

		mongoClient2.close();
*/
	}

}
