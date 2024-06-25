const app = getApp()

Page({
  data: {
    showIndex: 0, //0-功效 1-归经 2-处方
    pullStatus: 0, //0-收起 1-展开
    wh: 600,
    cateList: [],
    cateIndex: 0,
    list: [],
    scrollPos: '',
    page: 1,
    totalPage: 1,
    pageSize: 10,
    loading: false
  },
  onLoad(options) {
    wx.getSystemInfoAsync({
      success: (result) => {
        console.log(result)
        this.setData({
          wh: result.windowHeight - 40
        })
      }
    })
    this.listCategory()
    // this.listData()
  },
  pulldownChange(e) {
    if (e.target.id != 'herb') {
      return;
    }
    if (this.data.pullStatus == 0) {
      this.setData({
        pullStatus: 1
      })
    } else {
      this.setData({
        pullStatus: 0
      })
    }
  },
  showIndexChange(e) {
    console.log(e)
    let index = e.target.dataset.index
    this.setData({
      pullStatus: 0
    })
    if (this.data.showIndex == index) {
      return
    }
    this.setData({
      showIndex: index
    })
    this.listCategory()
  },
  cateIndexChange(e) {
    let cateIndex = e.target.dataset.index
    if (cateIndex == this.data.cateIndex) {
      return
    }
    this.setData({
      cateIndex: cateIndex
    })
    this.listData()
  },
  listCategory() {
    let index = this.data.showIndex
    let url
    if (index == 0) {
      url = '/herb/function/listParents'
    } else if (index == 1) {
      url = '/herb/tropism/listAll'
    } else {
      this.listData()
      return
    }
    app.request({
      url: url
    }).then(res => {
      console.log(res)
      this.setData({
        cateList: res.data.list
      })
    }).then(() => {
      this.listData()
    })
  },
  listData() {
    let index = this.data.showIndex
    if (index == 0) {
      this.listHerbByFunction()
    } else if (index == 1) {
      this.listHerbByTropism()
    } else {
      this.listPrescription()
    }
  },
  listHerbByFunction() {
    app.request({
      url: '/herb/herb/listByFunction',
      data: {
        functionId: this.data.cateList[this.data.cateIndex].id
      }
    }).then(res => {
      this.setData({
        data: res.data.data
      })
    })
  },
  listHerbByTropism() {
    app.request({
      url: '/herb/herb/listByTropism',
      data: {
        tropismId: this.data.cateList[this.data.cateIndex].id
      }
    }).then(res => {
      console.log(res)
      this.setData({
        data: res.data.data
      })
    })
  },
  listPrescription(page = 1) {
    if (this.loading) {
      return
    }
    this.setData({
      loading: true
    })
    if (page == 1) {
      this.setData({
        list: []
      })
    }
    wx.showLoading({
      title: '加载中'
    })
    app.request({
      url: '/herb/prescription/list',
      data: {
        page: page,
        limit: this.data.pageSize
      }
    }).then(res => {
      console.log(res)
      this.setData({
        page: res.data.page.currPage,
        totalPage: res.data.page.totalPage,
        list: [...this.data.list, ...res.data.page.list],
        loading: false
      })
      wx.hideLoading()
    })
  },
  listNextPage(e) {
    console.log(e)
    console.log('触底了哦')
    if (this.data.page >= this.data.totalPage) {
      wx.showToast({
        title: '已经到底了！'
      })
      return
    }
    this.listPrescription(this.data.page + 1)
  },
  toDetailPage(e) {
    console.log(e)
    let id = e.target.dataset.id
    let showIndex = this.data.showIndex
    if (showIndex == 2) {
      wx.navigateTo({
        url: '../prescription/info/info?id=' + id,
      })
    } else {

    }
  }
})