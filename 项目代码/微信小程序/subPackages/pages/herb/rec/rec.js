// subPackages/pages/herb/rec/rec.js
Page({
  data: {
    list: []
  },
  onLoad(options) {
    let list = JSON.parse(options.list)
    for (let item of list) {
      item.probability = Math.round(item.probability * 10000) / 100
    }
    this.setData({
      list: list
    })
  },
  toInfoPage(e) {
    let id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '../info/info?id=' + id,
    })
  }
})