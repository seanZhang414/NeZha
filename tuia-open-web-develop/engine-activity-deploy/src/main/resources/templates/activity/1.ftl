<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
  <meta name="format-detection" content="telephone=no"/>
  <title>信息流</title>
  <script>!function(e,i){var t=e.documentElement,n=navigator.userAgent.match(/iphone|ipod|ipad/gi),a=n?Math.min(i.devicePixelRatio,3):1,m="orientationchange"in window?"orientationchange":"resize";t.dataset.dpr=a;for(var d,l,c=!1,o=e.getElementsByTagName("meta"),r=0;r<o.length;r++)l=o[r],"viewport"==l.name&&(c=!0,d=l);if(c)d.content="width=device-width,initial-scale=1.0,maximum-scale=1.0, minimum-scale=1.0,user-scalable=no";else{var o=e.createElement("meta");o.name="viewport",o.content="width=device-width,initial-scale=1.0,maximum-scale=1.0, minimum-scale=1.0,user-scalable=no",t.firstElementChild.appendChild(o)}var s=function(){var e=t.clientWidth;e/a>640&&(e=640*a),window.remScale=e/640,t.style.fontSize=200*(e/640)+"px"};s(),e.addEventListener&&i.addEventListener(m,s,!1)}(document,window);</script>
    <link rel="stylesheet" href="http://yun.dui88.com/db-m/pkg/app/tuia-demo/xinxiliu.html_aio_93e97a0.css" />
    <style>
      html,body{height: 100%;overflow: hidden;}
      html,body,#db-content,.demo{background:none;}
      .demo{position: relative;}
      .demo-top-dialog{position: relative;top:0;left: 0;}
      .demo-top-img{height: 1.79rem;border:none;padding-top: 0.1rem;}
      .demo-top-img img{height: 1.2rem;}
      .demo-top-img .ad{margin-left: 0.02rem;}
    </style>
</head>
<body>
  <div id="db-content">
    <div class="demo">
      <div class="demo-top-dialog">
        <a class="demo-top-img" >
          <h2>${title!}</h2>
          <img src="${image!}">
          <#if adIconVisible> <i class="ad">广告</i> </#if>
        </a>
      </div>
    </div>
  </div>
</body>
</html>