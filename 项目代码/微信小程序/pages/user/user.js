
Page({
  data: {

  },
  onLoad(options) {

  },
  getAvatar(e) {
    console.log(111)
    wx.getUserProfile({
      desc: '获取内容',
      success: res => {
        console.log('res', res)
      }
    })
  }
})