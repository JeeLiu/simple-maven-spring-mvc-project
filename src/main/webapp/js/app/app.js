/**
 * Created by Sheldon Chen on 2014/10/1.
 */

'use strict';

function SetCookie(name, value)//两个参数，一个是cookie的名子，一个是值
{
    var Days = 30; //此 cookie 将被保存 30 天
    var exp = new Date();    //new Date("December 31, 9998");
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}

function GetCookie(name)//取cookies函数
{
    var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
    if (arr != null) return unescape(arr[2]);
    return null;
}

// Declare app level module which depends on filters, and services
var app = angular.module('SkeletonApp', [
    'ngRoute',
    'SkeletonApp.services',
    'SkeletonApp.controllers'
]);
