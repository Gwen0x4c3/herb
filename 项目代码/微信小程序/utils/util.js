const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

function parseTime(date) { 
  date = new Date(date).getTime()
  var time = new Date().getTime(); //去掉 js 时间戳后三位，与时间戳保持一致
  time = parseInt((time - date) / 1000); //存储转换值
  var s;
  if (time < 60 * 10) { //十分钟内
    return '刚刚';
  } else if (time < 60 * 60) { //超过十分钟少于1小时
    s = Math.floor(time / 60);
    return s + "分钟前";
  } else if (time < 60 * 60 * 24) {
    //超过1小时少于24小时
    s = Math.floor(time / 60 / 60);
    return s + "小时前";
  } else if (time < 60 * 60 * 24 * 3)  {
    //超过1天少于3天内
    s = Math.floor(time / 60 / 60 / 24);
    return s + "天前";
  } else {
    //超过3天
    var date = new Date(parseInt(date));
    let _month = appendZero(date.getMonth() + 1)
    let _date = appendZero(date.getDate())
    let _hour = appendZero(date.getHours())
    let _min = appendZero(date.getMinutes())
    let _sec = appendZero(date.getSeconds())
    return `${date.getFullYear()}-${_month}-${_date} ${_hour}:${_min}:${_sec}` 
  }
}

function appendZero(num) {
  if (num < 10) 
    return '0' + num;
  return num;
}

module.exports = {
  formatTime,
  parseTime
}
