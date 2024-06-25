const app = getApp()
Page({
  data: {
    currentPage: 1,
    totalPage: 0,
    keyword: '',
    searchType: 0,
    searchPanelVisible: false,
    list: [{
      title: '123123'
    }]
  },
  onLoad(options) {
    console.log(options)
    let keyword = options.keyword
    let searchType = options.searchType
    this.setData(options)
    this.search(keyword, searchType)
  },
  search(keyword, searchType) {
    this.selectComponent('#searchBar').search(keyword, searchType)
    console.log('search complete')
  },
  pageChange(e) {
    this.setData({
      currentPage: e.detail.currentPage
    })
    this.search(this.data.keyword, this.data.searchType)
  },
  toInfoPage(e) {
    console.log(e)
    let id = e.currentTarget.dataset.id
    let url
    if (this.data.searchType == 0) {
      url = '../../../subPackages/pages/herb/info/info?id=' + id
    } else if (this.data.searchType == 1) {
      url = '../../../subPackages/pages/prescription/info/info?id=' + id
    }
    wx.navigateTo({
      url: url
    })
  }
})