const app = getApp()

Page({
  data: {
    list: [],
    ids: [],
    keyword: '',
    hints: [],
    hintVisible: false,
    result: {}
  },
  onLoad(options) {
    let list = JSON.parse(options.list)
    this.setData({
      list: list
    })
  },
  handleFocus() {
    if (this.data.keyword && this.data.hints.length > 0) {
      this.setData({
        hintVisible: true
      })
    }
  },
  handleBlur() {
    this.setData({
      hintVisible: false
    })
  },
  handleInput(e) {
    console.log(e)
    if (this.data.keyword) {
      let exclude = []
      let list = this.data.list
      console.log(list)
      for (let herb of list) {
        exclude.push(herb.id)
      }
      let data = {name: this.data.keyword}
      if (exclude) {
        data.excludeIds = exclude + ''
      } 
      app.request({
        url: '/herb/herb/searchName',
        data: data,
      }).then(res=> {
        console.log(res)
        this.setData({
          hints: res.data.list,
          hintVisible: true
        })
      })
    } else {
      this.setData({
        hints: [],
        hintVisible: false
      })
    }
  },
  addHerb(e) {
    let index = e.currentTarget.dataset.index
    let list = this.data.list
    list.push(this.data.hints[index])
    this.setData({
      list: list,
      keyword: '',
      hints: [],
      hintVisible: false
    })
  },
  searchHerbNames() {
    let name = this.data.keyword
    app.request({
      url: '/herb/herb/searchName',
      data: {
        name: name
      }
    }).then(res => {
      console.log(res)
      this.setData({
        list: res.data.list
      })
    })
  },
  removeHerb(e) {
    let i = e.currentTarget.dataset.index
    console.log(i)
    let list = this.data.list
    list.splice(i, 1)
    this.setData({
      list: list
    })
  },
  analyze() {
    let ids = []
    let list = this.data.list
    for (let herb of list) {
      ids.push(herb.id)
    }
    app.request({
      url: '/herb/herb/analyze',
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded' 
      },
      data: {
        ids: ids
      }
    }).then(res => {
      console.log(res.data)
      let result = res.data.result
      result.symptoms = result.symptoms.join('，') + '。'
      result.functions = result.functions.join('，') + '。'
      this.setData({
        result: result
      })
    })
  },
  onReachBottom() {
    wx.showLoading({
      title: '加载中'
    })
    setTimeout(() => {
      wx.hideLoading()
    }, 1000);
  }
})