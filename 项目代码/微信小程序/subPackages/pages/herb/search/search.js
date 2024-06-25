const app = getApp()
Page({
  data: {
    imageSrc: '',
    list: []
  },
  onLoad(options) {

  },
  chooseImage: function() {
    var that = this
    wx.chooseImage({
      count: 1,
      success: function(res) {
        that.setData({
          imageSrc: res.tempFilePaths[0]
        })
        that.searchSimilarImages()
      }
    })
  },
  searchSimilarImages: function() {
    var that = this
    wx.showLoading({
      title: '搜索中',
    })
    wx.uploadFile({
      url: app.globalData.baseUrl + '/herb/herb/imageSearch',
      filePath: that.data.imageSrc,
      name: 'file',
      success: function(res) {
        wx.hideLoading()
        var data = JSON.parse(res.data)
        console.log(data)
        that.setData({
          list: data.data.list
        })
      },
      fail: function(res) {
        wx.hideLoading()
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
      }
    })
  }
})