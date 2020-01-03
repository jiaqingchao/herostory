package org.tinygame.herostory.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PackageUtil {
    /**
     * 私有默认构造器
     */
    private PackageUtil(){
        
    }

    /**
     * 列表指定包中的所有子类
     * @param packageName
     * @param recursive
     * @param superClazz
     * @return
     */
    public static Set<Class<?>> listSubClazz(
            String packageName, boolean recursive, Class<?> superClazz){
        if(superClazz == null){
            return Collections.emptySet();
        }
        
        return listClazz(packageName, recursive, superClazz::isAssignableFrom);
    }

    /**
     * 列表指定包中的所有类
     * @param packageName
     * @param recursive
     * @param filter
     * @return
     */
    private static Set<Class<?>> listClazz(
            String packageName, boolean recursive, IClazzFilter filter) {
        if(packageName == null || packageName.isEmpty()){
            return null;
        }

        //将点转换成斜杠
        final String packagePath = packageName.replace(".","/");
        //获取类加载器
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        //结果集合
        Set<Class<?>> resultSet = new HashSet<>();

        try{
            //获取URL 枚举
            Enumeration<URL> urlEnum = cl.getResources(packagePath);

            while (urlEnum.hasMoreElements()){
                //当前Url
                URL currUrl = urlEnum.nextElement();
                //协议文本
                final String protocol = currUrl.getProtocol();
                //临时集合
                Set<Class<?>> tmpSet = null;
                if("FILE".equalsIgnoreCase(protocol)){
                    //从文件系统中加载类
                    tmpSet = listClazzFromDir(
                            new File(currUrl.getFile()), packageName, recursive, filter
                    );
                }else if("JAR".equalsIgnoreCase(protocol)){
                    //获取文本字符串
                    String fileStr = currUrl.getFile();
                    if(fileStr.startsWith("file:")){
                        //如果以file:开头的，则去除这个歌开头
                        fileStr = fileStr.substring(5);
                    }
                    if(fileStr.lastIndexOf("!") > 0){
                        // 如果有'!'字符，则截断字符之后的所有字符
                        fileStr = fileStr.substring(0,fileStr.lastIndexOf("!"));
                    }
                    //从JAR文件加载类
                    tmpSet = listClazzFromJar(
                            new File(fileStr), packageName, recursive, filter
                    );
                }
                if(tmpSet != null){
                    //如果类集合不为空，则添加到结果中
                    resultSet.addAll(tmpSet);
                }
            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return resultSet;
    }

    private static Set<Class<?>> listClazzFromDir(
            File dirFile, String packageName, boolean recursive, IClazzFilter filter) {
        if(!dirFile.exists() || !dirFile.isDirectory()){
            //如果参数对象为空，直接退出
            return null;
        }
        //获取子文件列表
        File[] subFileArr = dirFile.listFiles();

        if(subFileArr == null || subFileArr.length <= 0){
            return null;
        }

        //文件列表，将子文件列表添加到队列
        Queue<File> fileQ = new LinkedList<>(Arrays.asList(subFileArr));

        //结果对象
        Set<Class<?>> resultSet = new HashSet<>();

        while (!fileQ.isEmpty()){
            //从队列中获取文件
            File currFile = fileQ.poll();

            if(currFile.isDirectory() && recursive){
                //如果当前文件是目录，病假执行递归操作时，获取子文件列表
                subFileArr = currFile.listFiles();
                if(subFileArr != null && subFileArr.length > 0){
                    //添加文件到队列
                    fileQ.addAll(Arrays.asList(subFileArr));
                }
                continue;
            }
            if(!currFile.isFile() || !currFile.getName().endsWith(".class")){
                //如果当前文件不是文件，或文件不是以.class结尾，则直接跳过
                continue;
            }

            //类名称
            String clazzName;

            //设置类名称
            clazzName = currFile.getAbsolutePath();
            //清除最后的 .class结尾
            clazzName = clazzName.substring(dirFile.getAbsolutePath().length(), clazzName.lastIndexOf("."));
            //转换目录斜杠
            clazzName = clazzName.replace("\\", "/");
            //清除开头的/
            clazzName = trimLeft(clazzName, "/");
            //将所有的/修改为.
            clazzName = join(clazzName.split("/"), ".");
            //包名 + 类名
            clazzName = packageName + "." + clazzName;

            try {
                //加载类定义
                Class<?> clazzObj = Class.forName(clazzName);

                if (filter != null && !filter.accept(clazzObj)) {
                    //如果过滤器不为空，且过滤器不接受当前类，则直接跳过
                    continue;
                }
                resultSet.add(clazzObj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        return resultSet;
    }
    private static Set<Class<?>> listClazzFromJar(
            final File jarFilePath, final String packageName,final boolean recursive, IClazzFilter filter) {

        if(!jarFilePath.exists()||jarFilePath.isDirectory()){
            //如果参数对象为空，直接退出
            return null;
        }

        //结果对象
        Set<Class<?>> resultSet = new HashSet<>();

        try {
            //创建 .jar文件读入流
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));
            //进入点
            JarEntry entry;

            while ((entry = jarIn.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                //获取进入点名称
                String entryName = entry.getName();
                if (!entryName.endsWith(".class")) {
                    //如果不是以.class结尾，则不是java文件，直接跳过！
                    continue;
                }
                if (!recursive) {
                    // 如果没有开启递归模式，那么久判断当前.class文件是否在指定目录下？
                    //获取目录名称
                    String tmpStr = entryName.substring(0, entryName.lastIndexOf("/"));
                    //将目录中的 "/"全部替换为"."
                    tmpStr = join(tmpStr.split("/"), ".");
                    if (!packageName.equals(tmpStr)) {
                        //如果包名和目录名不相等，则直接跳过
                        continue;
                    }

                }

                //类名称
                String clazzName;

                //清除最后的 .class结尾
                clazzName = entryName.substring(0, entryName.lastIndexOf("."));
                //将所有的/修改为.
                clazzName = join(clazzName.split("/"), ".");

                //加载类定义
                Class<?> clazzObj = Class.forName(clazzName);

                if (filter != null && !filter.accept(clazzObj)) {
                    //如果过滤器不为空，且过滤器不接受当前类，则直接跳过
                    continue;
                }
                resultSet.add(clazzObj);
            }
            //关闭输入流
            jarIn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return resultSet;
    }

    public interface IClazzFilter{
        /**
         * 是否接受当前类
         * @param clazz
         * @return 是否符合条件
         */
        boolean accept(Class<?> clazz);
    }

    /**
     * 使用连接符连接字符串数组
     *
     * @param strArr 字符串数组
     * @param conn   连接符
     * @return 连接后的字符串
     */
    static private String join(String[] strArr, String conn) {
        if(strArr == null || strArr.length == 0){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if(i > 0){
                sb.append(conn);
            }

            sb.append(strArr[i]);
        }
        return sb.toString();
    }

    /**
     * 清除源字符串左边的字符串
     *
     * @param str     原字符串
     * @param trimStr 需要被清除的字符串
     * @return 清除后的字符串
     */
    static private String trimLeft(String str, String trimStr) {
        if(str == null || str.isEmpty()){
            return "";
        }

        if(trimStr == null || trimStr.isEmpty()){
            return str;
        }


        if(str.equals(trimStr)){
            return "";
        }

        while (str.startsWith(trimStr)){
            str = str.substring(trimStr.length());
        }

        return str;
    }
}
