const app = getApp()

Page({
  data: {

  },
  onLoad(options) {

  },
  openCamera(e) {
    let type = e.currentTarget.dataset.type
    if (type == 2) {
      wx.navigateTo({
        url: `../../subPackages/pages/herb/search/search`
      })
      return;
    }
    wx.chooseMedia({
      count: 1,
      mediaType: 'image',
      camera: 'back',
      success: res => {
        console.log('success', res)
        let filePath = res.tempFiles[0].tempFilePath
        wx.showLoading({
          title: '识别中',
        })
        // let url = app.globalData.baseUrl + `/${type==0?'herb/herb':'ocr'}/recognize`
        let url = app.globalData.baseUrl
        switch(type) {
          case '0':
            url += '/herb/herb/recognize';
            break;
          case '1':
            url += '/ocr/recognize';
            break;
          case '2':
            url += '/herb/herb/imageSearch';
            break;
        }
        console.log('sending image to ' + url)
        wx.uploadFile({
          filePath: filePath,
          name: 'file',
          url: url,
          success: res => {
            wx.hideLoading()
            let data = JSON.parse(res.data).data
            let list = data.list
            wx.navigateTo({
              url: `../../subPackages/pages/${type==0?'herb':'prescription'}/rec/rec?list=${JSON.stringify(list)}`
            })
          },
          error: err => {
            console.log('error', err)
          }
        })
      },
      error: res => {
        console.log('error', res)
      }
    })
  }
})