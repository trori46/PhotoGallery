package com.example.demo.photogallery

import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.random.Random


class FlickrFetchr constructor() {
    private var mKeyWord: String? = null

    constructor(keyWord: String?) : this() {
        mKeyWord = keyWord
    }

    @Throws(IOException::class)
    fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        try {
            val out = ByteArrayOutputStream()
            val inputStream = connection.inputStream
            if (HttpURLConnection.HTTP_OK != connection.responseCode) {
                throw IOException(
                    "${connection.responseMessage}: with $urlSpec"
                )
            }
            val buffer = ByteArray(1024)
            var bytesRead = inputStream.read(buffer)
            while (bytesRead > 0) {
                out.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }
            out.close()
            return out.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }

    @Throws(IOException::class)
    fun getItems(urlSpec: String): ArrayList<GalleryItem> {
        val items = ArrayList<GalleryItem>()
        var existsItem: HashSet<Int> = HashSet<Int>()
        getUrlString(urlSpec)
        for (i in 1..20) {
            var id: UUID = UUID.randomUUID()
            var random: Int = Random(id.hashCode()).nextInt(1, i + 1)
            if (mKeyWord != null) {
                if (existsItem.contains(random)) {
                    continue
                }
                existsItem.add(random)
            }
            items.add(GalleryItem(id.toString(), "mCaption_$i", URLS[random]))
        }
        return items
    }

    companion object {
        private val URLS = arrayListOf<String>(
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542279788935&di=82cf72120d82db77f12fcc704f08cbb9&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2018-01-10%2F5a55d73d429a4.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752872&di=4a15b2c18a566e1d18f58afa02899ad0&imgtype=0&src=http%3A%2F%2Fp2.gexing.com%2FG1%2FM00%2F72%2F94%2FrBACFFXajkayn4d3ADaynVabCnI178.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542279788941&di=03c8ad365a1b05943d77608128926569&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2018-01-11%2F5a56d7216b099.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752869&di=ff226c50263e40e6968c1ddcb19a47fa&imgtype=0&src=http%3A%2F%2Fpic39.photophoto.cn%2F20160609%2F0017029508607829_b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542619838579&di=217135234335b630bf68317dde23dd87&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1101%2F18%2Fc2%2F6575505_6575505_1295354893250.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752871&di=44cd88cbfdcac54c8266d60107a6bf29&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201209%2F18%2F175007gt9mt8t4ymu6u6h9.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752871&di=fda639d3c320c8c65f2c2af3242dc0c2&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-12-09%2F5a2b747d882c1.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542619700624&di=0cdf7b8dae961e1203bf1deaebabb927&imgtype=0&src=http%3A%2F%2Fpic30.photophoto.cn%2F20140314%2F0011034492898514_b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542279788940&di=a80df8092b90f3702d765341354f9c99&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-11-30%2F5a1fcf0ec762c.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752867&di=d2898a984e303cff8ad3354e120be133&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fc%2F58804f1d79857.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752871&di=1149f15b8a2b1f06eabb428230a9f0d7&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-10-22%2F59ebfa941310b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542279788941&di=67e080262d4355501f37172be815911e&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2018-02-05%2F5a77d30b3267b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752867&di=d12dead644e9df0a93b8ad321c318b65&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fd%2F59ace388a110b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752871&di=c8def70f896d57bb844d74ddffa434c5&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-10-22%2F59ebfaa03bbba.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752871&di=e4b6ec13801e7185dfbd57557b49902d&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-10-22%2F59ebfa99376e2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752871&di=37376a7c60ee8550bd7aaf959de41b68&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F5%2F587715dbd144a.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752869&di=9c0ff02bc6e26b4c85154f9a1678645b&imgtype=0&src=http%3A%2F%2Fpic38.photophoto.cn%2F20160303%2F0005018459787147_b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752869&di=5f6c405b3a804702e3dff26696b33a06&imgtype=0&src=http%3A%2F%2Fpic29.photophoto.cn%2F20131101%2F0020032854675386_b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542279788940&di=5dd5d3cc1a5310e1c9b315b5acbe3dda&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2018-02-05%2F5a77d2f8c9e99.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752869&di=aeb387e0fdf26c22785a11ce318f5d90&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-10-12%2F59dece10253eb.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752869&di=efd6951e603b8e836a7e6a5db93e80a6&imgtype=0&src=http%3A%2F%2Fpic28.photophoto.cn%2F20130801%2F0020033097007454_b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752868&di=f57c584885e46690664f99f297160b10&imgtype=0&src=http%3A%2F%2Fimg4.xiazaizhijia.com%2Fwalls%2F20160711%2F1440x900_1a879f19454a5f8.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542278752869&di=848fc643152b27294637df6210c9a3b0&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-10-12%2F59dece1f6cd3e.jpg"
        )
    }
}