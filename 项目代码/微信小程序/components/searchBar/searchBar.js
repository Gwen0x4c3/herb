const app = getApp()

Component({
  properties: {
    keyword: String,
    searchType: Number,
    isIndex: Boolean,
    searchPanelVisible: {
      type: Boolean,
      value: false
    }
  },
  data: {
    pickerArray: ['找中药', '找药方', '找文章'],
    hints: [],
    hintsVisible: false,
    parent: null,
    hotwords: [],
    searchHistroy: []
  },
  lifetimes: {
    attached: function() {
      this.setParent()
      this.initSearchPanel(500)
    }
  },
  methods: {
    setParent() {
      if (this.data.parent == null) {
        let pages = getCurrentPages()
        this.setData({
          parent: pages[pages.length - 1]
        })
      }
    },
    initSearchPanel(delay = 0) {
      setTimeout(() => {
        this.loadHotWords()
        this.loadSearchHistroy()
      }, delay);
    },
    loadHotWords() {
      app.request({
        url: '/search/hotwords',
        data: {
          type: this.data.searchType
        }
      }).then(res => {
        console.log(res)
        this.setData({
          hotwords: res.data.list
        })
      })
    },
    loadSearchHistroy() {
      app.request({
        url: '/search/history',
        data: {
          type: this.data.searchType
        }
      }).then(res => {
        console.log(res)
        this.setData({
          searchHistory: res.data.list
        })
      })
    },
    setParentData(options) {
      this.data.parent.setData(options)
    },
    handleSearchConfirm(e) {
      this.search(e.detail.value, this.data.searchType)
    },
    handleSearchInput(e) {
      let value = e.detail.value
      this.setData({
        keyword: value
      })
      if (value == null || value.length == 0) {
        this.setData({
          hints: [],
          hintsVisible: false
        })
        return;
      } 
      this.searchForHints(value, this.data.searchType)
    },
    handleSearchBlur() {
      console.log(this.data)
      this.setData({
        hintsVisible: false
      })
    },
    handleSearchFocus() {
      console.log('打开SearchPanel')
      this.setParentData({
        searchPanelVisible: true
      })
      if (this.data.hints != null && this.data.hints.length != 0) {
        this.setData({
          hintsVisible: true
        })
      }
    },
    handlePickerChange(e) {
      this.initSearchPanel()
      if (this.data.keyword) {
        this.search(this.data.keyword, this.data.searchType)
      }
    },
    clearKeyword() {
      this.setData({
        keyword: '',
        hints: []
      })
    },
    closeSearchPanel() {
      this.setParentData({
        searchPanelVisible: false
      })
    },
    clearHistory() {
      wx.request({
        url: 'http://localhost:8002/search/history',
        method: 'DELETE',
        success: res => {
          wx.showToast({
            title: '清空成功',
            icon: 'success'
          })
          this.setData({
            searchHistory: []
          })
        }
      })
      this.setData({

      })
    },
    searchForHints(keyword, searchType) {
      //TODO 根据当前关键词搜索可能性高的选项
      app.request({
        url: '/search/searchForHints',
        method: "GET",
        data: {
          keyword: keyword,
          type: searchType
        }
      }).then(res => {
        console.log(res)
          if (this.data.keyword) {
            this.setData({
              hints: res.data.list,
              hintsVisible: true
            }) 
          }
      }).catch(err => {
        wx.showToast({
          title: '网络延迟',
        })
      })
    },
    handleHintClick(e) {
      this.closeSearchPanel()
      let keyword = e.target.dataset.keyword
      this.setData({
        keyword: keyword,
        hints: []
      })
      this.search(keyword, this.data.searchType)
    },
    search(keyword, searchType) {
      console.log(this.data)
      if (this.data.isIndex) {
        wx.navigateTo({
          url: `/subPackages/pages/search/search?keyword=${keyword}&searchType=${searchType}`
        })
      } else {
        wx.showLoading({
          title: '加载中',
        })
        app.request({
          url: '/search',
          data: {
            keyword: keyword,
            type: searchType,
            page: 1
          }
        }).then(res => {
          console.log(res)
          let list = res.data.page.list
          if (searchType == 0) {
           
          } else if (searchType == 1) {
            
          }
          this.setParentData({
            totalPage: res.data.page.totalPage,
            currentPage: res.data.page.page,
            list: res.data.page.list,
            searchPanelVisible: false
          })
          this.setData({
            searchPanelVisible: false
          })
          wx.hideLoading({
            success: (res) => {},
          })
        })
      }
    },
    searchBlockTap(e) {
      this.closeSearchPanel()
      this.setData({
        keyword: e.target.dataset.keyword,
        list: [],
        currentPage: 0,
        totalPage: 0,
        searchPanelVisible: false
      })
      this.search(e.target.dataset.keyword, this.data.searchType)
    }
  }
})
