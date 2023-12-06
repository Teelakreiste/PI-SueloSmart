<helpset version="1.0">
    <title>Manual de usuario SueloSmart</title>
    <maps>
        <homeID>introduccion</homeID>
        <mapref location="map_file.jhm"/>
    </maps>

    <view>
        <name>Tabla de contenidos</name>
        <label>Tabla de contenidos</label>
        <type>javax.help.TOCView</type>
        <data>toc.xml</data>
    </view>

    <view>
        <name>Indice</name>
        <label>Indice</label>
        <type>javax.help.IndexView</type>
        <data>indice.xml</data>
    </view>

    <view>
        <name>Buscar</name>
        <label>Buscar</label>
        <type>javax.help.SearchView</type>
        <data engine="com.sun.java.help.search.DefaultSearchEngine">
            JavaHelpSearch
        </data>
    </view>
</helpset>