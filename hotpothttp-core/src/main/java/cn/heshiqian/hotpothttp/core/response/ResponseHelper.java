package cn.heshiqian.hotpothttp.core.response;

import cn.heshiqian.hotpothttp.core.FileCache;
import cn.heshiqian.hotpothttp.core.Version;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.exception.HotPotHttpException;
import cn.heshiqian.hotpothttp.core.exception.WhatTheFxxk;
import cn.heshiqian.hotpothttp.core.pojo.CacheFilePojo;
import cn.heshiqian.hotpothttp.core.pojo.Package;
import cn.heshiqian.hotpothttp.core.pojo.SimpleFilePojo;
import cn.heshiqian.hotpothttp.core.request.Request;
import cn.heshiqian.hotpothttp.core.request.RequestHead;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ResponseHelper {

    public static final Package.PackageType hphResponsePT=new Package.PackageType("hotpothttp-response-package-type","");
    public static final byte[] newline="\r\n".getBytes();

    private static final Map<String,String> mime;

    static{
        mime = new HashMap<>();
        String confDir = Configuration.getArg(Configuration.CONF_DIR, String.class);
        if (confDir==null)throw new WhatTheFxxk("conf dir configuration not in System");
        File file = new File(confDir + File.separator + "mime.properties");
        Properties properties = new Properties();
        if (file.exists()){
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            String content=".*#%application/octet-stream\n" +
                    ".tif#%image/tiff\n" +
                    ".001#%application/x-001\n" +
                    ".301#%application/x-301\n" +
                    ".323#%text/h323\n" +
                    ".906#%application/x-906\n" +
                    ".907#%drawing/907\n" +
                    ".a11#%application/x-a11\n" +
                    ".acp#%audio/x-mei-aac\n" +
                    ".ai#%application/postscript\n" +
                    ".aif#%audio/aiff\n" +
                    ".aifc#%audio/aiff\n" +
                    ".aiff#%audio/aiff\n" +
                    ".anv#%application/x-anv\n" +
                    ".asa#%text/asa\n" +
                    ".asf#%video/x-ms-asf\n" +
                    ".asp#%text/asp\n" +
                    ".asx#%video/x-ms-asf\n" +
                    ".au#%audio/basic\n" +
                    ".avi#%video/avi\n" +
                    ".awf#%application/vnd.adobe.workflow\n" +
                    ".biz#%text/xml\n" +
                    ".bmp#%application/x-bmp\n" +
                    ".bot#%application/x-bot\n" +
                    ".c4t#%application/x-c4t\n" +
                    ".c90#%application/x-c90\n" +
                    ".cal#%application/x-cals\n" +
                    ".cat#%application/vnd.ms-pki.seccat\n" +
                    ".cdf#%application/x-netcdf\n" +
                    ".cdr#%application/x-cdr\n" +
                    ".cel#%application/x-cel\n" +
                    ".cer#%application/x-x509-ca-cert\n" +
                    ".cg4#%application/x-g4\n" +
                    ".cgm#%application/x-cgm\n" +
                    ".cit#%application/x-cit\n" +
                    ".class#%java/*\n" +
                    ".cml#%text/xml\n" +
                    ".cmp#%application/x-cmp\n" +
                    ".cmx#%application/x-cmx\n" +
                    ".cot#%application/x-cot\n" +
                    ".crl#%application/pkix-crl\n" +
                    ".crt#%application/x-x509-ca-cert\n" +
                    ".csi#%application/x-csi\n" +
                    ".css#%text/css\n" +
                    ".cut#%application/x-cut\n" +
                    ".dbf#%application/x-dbf\n" +
                    ".dbm#%application/x-dbm\n" +
                    ".dbx#%application/x-dbx\n" +
                    ".dcd#%text/xml\n" +
                    ".dcx#%application/x-dcx\n" +
                    ".der#%application/x-x509-ca-cert\n" +
                    ".dgn#%application/x-dgn\n" +
                    ".dib#%application/x-dib\n" +
                    ".dll#%application/x-msdownload\n" +
                    ".doc#%application/msword\n" +
                    ".dot#%application/msword\n" +
                    ".drw#%application/x-drw\n" +
                    ".dtd#%text/xml\n" +
                    ".dwf#%Model/vnd.dwf\n" +
                    ".dwf#%application/x-dwf\n" +
                    ".dwg#%application/x-dwg\n" +
                    ".dxb#%application/x-dxb\n" +
                    ".dxf#%application/x-dxf\n" +
                    ".edn#%application/vnd.adobe.edn\n" +
                    ".emf#%application/x-emf\n" +
                    ".eml#%message/rfc822\n" +
                    ".ent#%text/xml\n" +
                    ".epi#%application/x-epi\n" +
                    ".eps#%application/x-ps\n" +
                    ".eps#%application/postscript\n" +
                    ".etd#%application/x-ebx\n" +
                    ".exe#%application/x-msdownload\n" +
                    ".fax#%image/fax\n" +
                    ".fdf#%application/vnd.fdf\n" +
                    ".fif#%application/fractals\n" +
                    ".fo#%text/xml\n" +
                    ".frm#%application/x-frm\n" +
                    ".g4#%application/x-g4\n" +
                    ".gbr#%application/x-gbr\n" +
                    ".#%application/x-\n" +
                    ".gif#%image/gif\n" +
                    ".gl2#%application/x-gl2\n" +
                    ".gp4#%application/x-gp4\n" +
                    ".hgl#%application/x-hgl\n" +
                    ".hmr#%application/x-hmr\n" +
                    ".hpg#%application/x-hpgl\n" +
                    ".hpl#%application/x-hpl\n" +
                    ".hqx#%application/mac-binhex40\n" +
                    ".hrf#%application/x-hrf\n" +
                    ".hta#%application/hta\n" +
                    ".htc#%text/x-component\n" +
                    ".htm#%text/html\n" +
                    ".html#%text/html\n" +
                    ".htt#%text/webviewhtml\n" +
                    ".htx#%text/html\n" +
                    ".icb#%application/x-icb\n" +
                    ".ico#%image/x-icon\n" +
                    ".ico#%application/x-ico\n" +
                    ".iff#%application/x-iff\n" +
                    ".ig4#%application/x-g4\n" +
                    ".igs#%application/x-igs\n" +
                    ".iii#%application/x-iphone\n" +
                    ".img#%application/x-img\n" +
                    ".ins#%application/x-internet-signup\n" +
                    ".isp#%application/x-internet-signup\n" +
                    ".IVF#%video/x-ivf\n" +
                    ".java#%java/*\n" +
                    ".jfif#%image/jpeg\n" +
                    ".jpe#%image/jpeg\n" +
                    ".jpe#%application/x-jpe\n" +
                    ".jpeg#%image/jpeg\n" +
                    ".jpg#%image/jpeg\n" +
                    ".jpg#%application/x-jpg\n" +
                    ".js#%application/x-javascript\n" +
                    ".jsp#%text/html\n" +
                    ".la1#%audio/x-liquid-file\n" +
                    ".lar#%application/x-laplayer-reg\n" +
                    ".latex#%application/x-latex\n" +
                    ".lavs#%audio/x-liquid-secure\n" +
                    ".lbm#%application/x-lbm\n" +
                    ".lmsff#%audio/x-la-lms\n" +
                    ".ls#%application/x-javascript\n" +
                    ".ltr#%application/x-ltr\n" +
                    ".m1v#%video/x-mpeg\n" +
                    ".m2v#%video/x-mpeg\n" +
                    ".m3u#%audio/mpegurl\n" +
                    ".m4e#%video/mpeg4\n" +
                    ".mac#%application/x-mac\n" +
                    ".man#%application/x-troff-man\n" +
                    ".math#%text/xml\n" +
                    ".mdb#%application/msaccess\n" +
                    ".mdb#%application/x-mdb\n" +
                    ".mfp#%application/x-shockwave-flash\n" +
                    ".mht#%message/rfc822\n" +
                    ".mhtml#%message/rfc822\n" +
                    ".mi#%application/x-mi\n" +
                    ".mid#%audio/mid\n" +
                    ".midi#%audio/mid\n" +
                    ".mil#%application/x-mil\n" +
                    ".mml#%text/xml\n" +
                    ".mnd#%audio/x-musicnet-download\n" +
                    ".mns#%audio/x-musicnet-stream\n" +
                    ".mocha#%application/x-javascript\n" +
                    ".movie#%video/x-sgi-movie\n" +
                    ".mp1#%audio/mp1\n" +
                    ".mp2#%audio/mp2\n" +
                    ".mp2v#%video/mpeg\n" +
                    ".mp3#%audio/mp3\n" +
                    ".mp4#%video/mpeg4\n" +
                    ".mpa#%video/x-mpg\n" +
                    ".mpd#%application/vnd.ms-project\n" +
                    ".mpe#%video/x-mpeg\n" +
                    ".mpeg#%video/mpg\n" +
                    ".mpg#%video/mpg\n" +
                    ".mpga#%audio/rn-mpeg\n" +
                    ".mpp#%application/vnd.ms-project\n" +
                    ".mps#%video/x-mpeg\n" +
                    ".mpt#%application/vnd.ms-project\n" +
                    ".mpv#%video/mpg\n" +
                    ".mpv2#%video/mpeg\n" +
                    ".mpw#%application/vnd.ms-project\n" +
                    ".mpx#%application/vnd.ms-project\n" +
                    ".mtx#%text/xml\n" +
                    ".mxp#%application/x-mmxp\n" +
                    ".net#%image/pnetvue\n" +
                    ".nrf#%application/x-nrf\n" +
                    ".nws#%message/rfc822\n" +
                    ".odc#%text/x-ms-odc\n" +
                    ".out#%application/x-out\n" +
                    ".p10#%application/pkcs10\n" +
                    ".p12#%application/x-pkcs12\n" +
                    ".p7b#%application/x-pkcs7-certificates\n" +
                    ".p7c#%application/pkcs7-mime\n" +
                    ".p7m#%application/pkcs7-mime\n" +
                    ".p7r#%application/x-pkcs7-certreqresp\n" +
                    ".p7s#%application/pkcs7-signature\n" +
                    ".pc5#%application/x-pc5\n" +
                    ".pci#%application/x-pci\n" +
                    ".pcl#%application/x-pcl\n" +
                    ".pcx#%application/x-pcx\n" +
                    ".pdf#%application/pdf\n" +
                    ".pdf#%application/pdf\n" +
                    ".pdx#%application/vnd.adobe.pdx\n" +
                    ".pfx#%application/x-pkcs12\n" +
                    ".pgl#%application/x-pgl\n" +
                    ".pic#%application/x-pic\n" +
                    ".pko#%application/vnd.ms-pki.pko\n" +
                    ".pl#%application/x-perl\n" +
                    ".plg#%text/html\n" +
                    ".pls#%audio/scpls\n" +
                    ".plt#%application/x-plt\n" +
                    ".png#%image/png\n" +
                    ".png#%application/x-png\n" +
                    ".pot#%application/vnd.ms-powerpoint\n" +
                    ".ppa#%application/vnd.ms-powerpoint\n" +
                    ".ppm#%application/x-ppm\n" +
                    ".pps#%application/vnd.ms-powerpoint\n" +
                    ".ppt#%application/vnd.ms-powerpoint\n" +
                    ".ppt#%application/x-ppt\n" +
                    ".pr#%application/x-pr\n" +
                    ".prf#%application/pics-rules\n" +
                    ".prn#%application/x-prn\n" +
                    ".prt#%application/x-prt\n" +
                    ".ps#%application/x-ps\n" +
                    ".ps#%application/postscript\n" +
                    ".ptn#%application/x-ptn\n" +
                    ".pwz#%application/vnd.ms-powerpoint\n" +
                    ".r3t#%text/vnd.rn-realtext3d\n" +
                    ".ra#%audio/vnd.rn-realaudio\n" +
                    ".ram#%audio/x-pn-realaudio\n" +
                    ".ras#%application/x-ras\n" +
                    ".rat#%application/rat-file\n" +
                    ".rdf#%text/xml\n" +
                    ".rec#%application/vnd.rn-recording\n" +
                    ".red#%application/x-red\n" +
                    ".rgb#%application/x-rgb\n" +
                    ".rjs#%application/vnd.rn-realsystem-rjs\n" +
                    ".rjt#%application/vnd.rn-realsystem-rjt\n" +
                    ".rlc#%application/x-rlc\n" +
                    ".rle#%application/x-rle\n" +
                    ".rm#%application/vnd.rn-realmedia\n" +
                    ".rmf#%application/vnd.adobe.rmf\n" +
                    ".rmi#%audio/mid\n" +
                    ".rmj#%application/vnd.rn-realsystem-rmj\n" +
                    ".rmm#%audio/x-pn-realaudio\n" +
                    ".rmp#%application/vnd.rn-rn_music_package\n" +
                    ".rms#%application/vnd.rn-realmedia-secure\n" +
                    ".rmvb#%application/vnd.rn-realmedia-vbr\n" +
                    ".rmx#%application/vnd.rn-realsystem-rmx\n" +
                    ".rnx#%application/vnd.rn-realplayer\n" +
                    ".rp#%image/vnd.rn-realpix\n" +
                    ".rpm#%audio/x-pn-realaudio-plugin\n" +
                    ".rsml#%application/vnd.rn-rsml\n" +
                    ".rt#%text/vnd.rn-realtext\n" +
                    ".rtf#%application/msword\n" +
                    ".rtf#%application/x-rtf\n" +
                    ".rv#%video/vnd.rn-realvideo\n" +
                    ".sam#%application/x-sam\n" +
                    ".sat#%application/x-sat\n" +
                    ".sdp#%application/sdp\n" +
                    ".sdw#%application/x-sdw\n" +
                    ".sit#%application/x-stuffit\n" +
                    ".slb#%application/x-slb\n" +
                    ".sld#%application/x-sld\n" +
                    ".slk#%drawing/x-slk\n" +
                    ".smi#%application/smil\n" +
                    ".smil#%application/smil\n" +
                    ".smk#%application/x-smk\n" +
                    ".snd#%audio/basic\n" +
                    ".sol#%text/plain\n" +
                    ".sor#%text/plain\n" +
                    ".spc#%application/x-pkcs7-certificates\n" +
                    ".spl#%application/futuresplash\n" +
                    ".spp#%text/xml\n" +
                    ".ssm#%application/streamingmedia\n" +
                    ".sst#%application/vnd.ms-pki.certstore\n" +
                    ".stl#%application/vnd.ms-pki.stl\n" +
                    ".stm#%text/html\n" +
                    ".sty#%application/x-sty\n" +
                    ".svg#%text/xml\n" +
                    ".swf#%application/x-shockwave-flash\n" +
                    ".tdf#%application/x-tdf\n" +
                    ".tg4#%application/x-tg4\n" +
                    ".tga#%application/x-tga\n" +
                    ".tif#%image/tiff\n" +
                    ".tif#%application/x-tif\n" +
                    ".tiff#%image/tiff\n" +
                    ".tld#%text/xml\n" +
                    ".top#%drawing/x-top\n" +
                    ".torrent#%application/x-bittorrent\n" +
                    ".tsd#%text/xml\n" +
                    ".txt#%text/plain\n" +
                    ".uin#%application/x-icq\n" +
                    ".uls#%text/iuls\n" +
                    ".vcf#%text/x-vcard\n" +
                    ".vda#%application/x-vda\n" +
                    ".vdx#%application/vnd.visio\n" +
                    ".vml#%text/xml\n" +
                    ".vpg#%application/x-vpeg005\n" +
                    ".vsd#%application/vnd.visio\n" +
                    ".vsd#%application/x-vsd\n" +
                    ".vss#%application/vnd.visio\n" +
                    ".vst#%application/vnd.visio\n" +
                    ".vst#%application/x-vst\n" +
                    ".vsw#%application/vnd.visio\n" +
                    ".vsx#%application/vnd.visio\n" +
                    ".vtx#%application/vnd.visio\n" +
                    ".vxml#%text/xml\n" +
                    ".wav#%audio/wav\n" +
                    ".wax#%audio/x-ms-wax\n" +
                    ".wb1#%application/x-wb1\n" +
                    ".wb2#%application/x-wb2\n" +
                    ".wb3#%application/x-wb3\n" +
                    ".wbmp#%image/vnd.wap.wbmp\n" +
                    ".wiz#%application/msword\n" +
                    ".wk3#%application/x-wk3\n" +
                    ".wk4#%application/x-wk4\n" +
                    ".wkq#%application/x-wkq\n" +
                    ".wks#%application/x-wks\n" +
                    ".wm#%video/x-ms-wm\n" +
                    ".wma#%audio/x-ms-wma\n" +
                    ".wmd#%application/x-ms-wmd\n" +
                    ".wmf#%application/x-wmf\n" +
                    ".wml#%text/vnd.wap.wml\n" +
                    ".wmv#%video/x-ms-wmv\n" +
                    ".wmx#%video/x-ms-wmx\n" +
                    ".wmz#%application/x-ms-wmz\n" +
                    ".wp6#%application/x-wp6\n" +
                    ".wpd#%application/x-wpd\n" +
                    ".wpg#%application/x-wpg\n" +
                    ".wpl#%application/vnd.ms-wpl\n" +
                    ".wq1#%application/x-wq1\n" +
                    ".wr1#%application/x-wr1\n" +
                    ".wri#%application/x-wri\n" +
                    ".wrk#%application/x-wrk\n" +
                    ".ws#%application/x-ws\n" +
                    ".ws2#%application/x-ws\n" +
                    ".wsc#%text/scriptlet\n" +
                    ".wsdl#%text/xml\n" +
                    ".wvx#%video/x-ms-wvx\n" +
                    ".xdp#%application/vnd.adobe.xdp\n" +
                    ".xdr#%text/xml\n" +
                    ".xfd#%application/vnd.adobe.xfd\n" +
                    ".xfdf#%application/vnd.adobe.xfdf\n" +
                    ".xhtml#%text/html\n" +
                    ".xls#%application/vnd.ms-excel\n" +
                    ".xls#%application/x-xls\n" +
                    ".xlw#%application/x-xlw\n" +
                    ".xml#%text/xml\n" +
                    ".xpl#%audio/scpls\n" +
                    ".xq#%text/xml\n" +
                    ".xql#%text/xml\n" +
                    ".xquery#%text/xml\n" +
                    ".xsd#%text/xml\n" +
                    ".xsl#%text/xml\n" +
                    ".xslt#%text/xml\n" +
                    ".xwd#%application/x-xwd\n" +
                    ".x_b#%application/x-x_b\n" +
                    ".sis#%application/vnd.symbian.install\n" +
                    ".sisx#%application/vnd.symbian.install\n" +
                    ".x_t#%application/x-x_t\n" +
                    ".ipa#%application/vnd.iphone\n" +
                    ".apk#%application/vnd.android.package-archive\n" +
                    ".xap#%application/x-silverlight-app\n";
            try {
                file.createNewFile();
                Map<String, String> map = new HashMap<>();
                String[] split = content.split("\n");
                for (int i = 0; i < split.length; i++) {
                    String[] kv = split[i].split("#%");
                    if (kv.length!=2)continue;
                    map.put(kv[0],kv[1]);
                }
                properties.putAll(map);
                properties.store(new FileOutputStream(file),"mime type");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        Iterator<Map.Entry<Object, Object>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<Object, Object> next = iterator.next();
            mime.put(next.getKey().toString(),next.getValue().toString());
        }
    }

    private static FileCache fileCache = FileCache.getInstance();

    public static void notSupport(Socket accept) {
        if (accept.isClosed()) throw new HotPotHttpException("socket is close");
        try {
            OutputStream outputStream = accept.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write("HTTP/1.1 405 NOT SUPPORT");
            bufferedWriter.newLine();
            bufferedWriter.write("Server:HotPotHttp "+Version.version);
            bufferedWriter.newLine();
            bufferedWriter.write("Content-Type:text/html");
            bufferedWriter.newLine();
            bufferedWriter.write("Connection:close");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("Method Not Support");
            bufferedWriter.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Response parseResponse(Request request){
        Map<Package.PackageType, Package> packages = request.getPackages();
        String path = request.getPath();
        boolean hasFile = fileCache.hasFile(path);
        ResponseImpl response = new ResponseImpl();
        if (hasFile){
            response.setStatus(200);
            response.setPath(request.getPath());
            String cc = request.getHeader().getHeader("Cache-Control");
            if (cc==null) cc="";
            response.setCacheControl(!cc.equalsIgnoreCase("no-cache"));
            CacheFilePojo cacheFilePojo;
            try {
                cacheFilePojo = fileCache.loadFile(path, response.getCacheControl());
            } catch (IOException e) {
                response.setStatus(500);
                e.printStackTrace();
                return response;
            }
            Package<CacheFilePojo> cacheFilePojoPackage = new Package<>();
            cacheFilePojoPackage.setOwnObj(cacheFilePojo);
            response.addPackage(hphResponsePT,cacheFilePojoPackage);
            response.addPackages(packages);
            response.addHeader("Content-Type", getMIME(cacheFilePojo.getSimpleFilePojo()));
            response.addHeader("Server","HotPotHttp_"+Version.version);
            response.addHeader("Connection","close");
        }else {
            response.setStatus(404);
        }
        return response;
    }

    private static String getMIME(SimpleFilePojo simpleFilePojo){
        String fileName = simpleFilePojo.getFileName();
        String end = fileName.substring(fileName.lastIndexOf("."));
        String MT = mime.get(end);
        if (MT==null)
            return "application/octet-stream";
        return MT;
    }

    public static void send(HttpSend httpSend, OutputStream outputStream) throws IOException{
        outputStream.write(httpSend.getHttp().getBytes());
        outputStream.write(httpSend.getHeader().getBytes());
        outputStream.write(newline);
        InputStream stream = httpSend.getStream();
        byte[] payload = httpSend.getPayload();
        if (stream!=null){
            //写流数据
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            BufferedInputStream bis = new BufferedInputStream(stream);
            byte[] buff=new byte[500];
            int len=0;
            while ((len=bis.read(buff))!=-1){
                bos.write(buff,0,len);
                bos.flush();
            }
            bos.close();
            bis.close();
        } else if (payload!=null){
            //写payload
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            ByteArrayInputStream bais = new ByteArrayInputStream(payload);
            byte[] buff=new byte[1024];
            int len=0;
            while ((len=bais.read(buff))!=-1){
                bos.write(buff,0,len);
                bos.flush();
            }
            bos.close();
            bais.close();
        } else {
            outputStream.write(" ".getBytes());
        }
        outputStream.close();
    }
}
