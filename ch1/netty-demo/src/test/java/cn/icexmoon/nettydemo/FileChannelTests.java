package cn.icexmoon.nettydemo;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @ClassName FileBufferTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/11/9 下午4:48
 * @Version 1.0
 */
public class FileChannelTests {
    private static final String DIR_PATH = "D:\\workspace\\learn-netty\\ch1\\netty-demo\\src\\test\\resources";

    @Test
    public void testRandomAccessFile() throws IOException {
        // 通过 RandomAccessFile 获取到可读可写的 ByteBuffer
        String filePath = DIR_PATH + "\\source.txt";
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            FileChannel channel = randomAccessFile.getChannel();
            // 读取并打印
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            long size = channel.size();
            System.out.println("size:" + size);
            int read;
            do {
                read = channel.read(byteBuffer);
                byteBuffer.flip();
                String content = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                System.out.println(content);
                byteBuffer.clear();
                long position = channel.position();
                System.out.println("position:" + position);
            }
            while (read != -1);
            // 写入文件内容
            channel.write(ByteBuffer.wrap("\nhello world\n".getBytes()));
        }
    }

    @Test
    public void testTwoChannelTransfer() throws IOException {
        // 两个文件通道之间进行数据传输
        String srcFilePath = DIR_PATH + "\\source.txt";
        String destFilePath = DIR_PATH + "\\target.txt";
        try (FileChannel srcChannel = new FileInputStream(srcFilePath).getChannel();
             FileChannel destChannel = new FileOutputStream(destFilePath).getChannel()) {
            long position = 0;
            do {
                long transferred = srcChannel.transferTo(position, srcChannel.size(), destChannel);
                if (transferred == 0) {
                    break;
                }
                position += transferred;
            }
            while (true);
        }
    }

    @Test
    public void testCreateDir() throws IOException {
        String dirPath = DIR_PATH + "\\dir";
        Path path = Paths.get(dirPath);
        Files.createDirectory(path);
    }

    @Test
    public void testCreateDirs() throws IOException {
        String dirPath = DIR_PATH + "\\dir\\dir1\\dir2";
        Path path = Paths.get(dirPath);
        Files.createDirectories(path);
    }

    @Test
    public void testFileCopy() throws IOException {
        Files.copy(Paths.get(DIR_PATH + "\\source.txt"), Paths.get(DIR_PATH + "\\target.txt"));
    }

    @Test
    public void testFileCopy2() throws IOException {
        Files.copy(Paths.get(DIR_PATH + "\\source.txt"), Paths.get(DIR_PATH + "\\target.txt"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testFileMove() throws IOException {
        Files.move(Paths.get(DIR_PATH + "\\test2.txt"), Paths.get(DIR_PATH + "\\test5.txt"));
    }

    @Test
    public void testDelFile() throws IOException {
        Files.delete(Paths.get(DIR_PATH + "\\test5.txt"));
    }

    @Test
    public void testDelDir() throws IOException {
        Files.delete(Paths.get(DIR_PATH + "\\dir"));
    }

    @Test
    public void testWalkDir() throws IOException {
        Files.walkFileTree(Paths.get(DIR_PATH), new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("=======>dir:" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("=======>file:" + file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("file:" + file + " error:" + exc);
                return super.visitFileFailed(file, exc);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("<=======dir:" + dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    @Test
    public void testDelDirs() throws IOException {
        // 删除多级目录
        Files.walkFileTree(Paths.get(DIR_PATH+"\\dir"), new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 删除文件
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 删除目录
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    @Test
    public void testDirCopy() throws IOException {
        // 复制多级目录
        String sourceDir = DIR_PATH+"\\test1";
        String targetDir = DIR_PATH+"\\test2";
        Files.walk(Paths.get(sourceDir) ).forEach(path -> {
            String targetPath = path.toString().replace(sourceDir, targetDir);
            try {
                Files.copy(path, Paths.get(targetPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
