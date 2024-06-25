const app = getApp()

Page({
  data: {
    herb: {}
  },
  onLoad(options) {
    let id = options.id
    app.request({
      url: `/herb/herb/info/${id}`
    }).then(res => {
      console.log(res)
      let herb = res.data.herb
      let paragraphs = []
      let json = JSON.parse(herb.text)
      for (let key in json) {
        paragraphs.push(`【${key}】${json[key]}`)
      }
      herb.paragraphs = paragraphs
      herb.text = ''
      this.setData({
        herb: res.data.herb
      })
    })
  }
})