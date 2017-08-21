package com.fz.googleplayteach.http.protocol;

import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.IOUtils;
import com.fz.googleplayteach.utils.StringUtils;
import com.fz.googleplayteach.utils.UIUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by 冯政 on 2017/6/27.
 * 访问网络的基类
 */

public abstract class BaseProtocol<T> {

    //index表示的是从哪个位置开始返回20条数据，用于分页
    public T getData(int index){
        //先判断是否有缓存，有的话就加载缓存
        String result=getCache(index);

        if (StringUtils.isEmpty(result)){//若果没有缓存，或者缓存失效
            //请求服务器
            result=getDataFromServer(index);
        }

        //开始解析
        if (result!=null){
            T data=parseData(result);
            return data;
        }
        return null;
    }

    //从网络获取数据
    //index表示的是从哪个位置开始返回20条数据，用于分页
    public String getDataFromServer(int index) {
        System.out.println("请求链接："+HttpHelper.URL+getKey()+"?index="+index+getParams());
        HttpHelper.HttpResult httpResult=HttpHelper.get(HttpHelper.URL+getKey()+"?index="+index+getParams());
        if (httpResult!=null){
            String result=httpResult.getString();
            System.out.println("访问结果："+result);
            //写缓存
            if (!StringUtils.isEmpty(result)) {
                setCache(index, result);
            }
            return result;
        }
        return null;
    }

    //获取网络关键词，子类必须实现
    public abstract String getKey();

    //获取网络连接参数，子类必须实现
    public abstract String getParams();

    //写缓存
    //以url为key,json为value
    public void setCache(int index,String json){
        //以url为文件名，以json为文件内容，保存在本地
        File cacheDir=UIUtils.getContext().getCacheDir();//本地应用的缓存文件夹

        //生成缓存文件
        File cacheFile=new File(cacheDir,getKey()+"?index="+index+getParams());

        FileWriter writer=null;
        try {
            writer=new FileWriter(cacheFile);
            //缓存失效的截止时间
            long deadline=System.currentTimeMillis()+60*60*1000;//一个小时有效期
            writer.write(deadline+"\n");//在一行写入缓存时间,换行

            writer.write(json);//写入json
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(writer);
        }
    }

    //读缓存
    public String getCache(int index){
        //以url为文件名，以json为文件内容，保存在本地
        File cacheDir=UIUtils.getContext().getCacheDir();//本地应用的缓存文件夹

        //生成缓存文件
        File cacheFile=new File(cacheDir,getKey()
                +"?index="+index+getParams());

        //判断缓存是否存在
        if (cacheFile.exists()){
            //判断缓存是否有效
            BufferedReader reader = null;
            try {
                reader=new BufferedReader(new FileReader(cacheFile));
                String deadLine=reader.readLine();

                long deadTime=Long.parseLong(deadLine);
                if (System.currentTimeMillis()<deadTime){//当前时间小于截止时间，说明缓存有效
                    //缓存有效
                    String line;
                    StringBuffer buffer=new StringBuffer();
                    while ((line=reader.readLine())!=null){
                        buffer.append(line);
                    }
                    return buffer.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                IOUtils.close(reader);
            }
        }
        return null;
    }

    public abstract T parseData(String result);
}
