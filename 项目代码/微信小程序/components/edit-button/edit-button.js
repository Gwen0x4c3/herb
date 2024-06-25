Component({
  properties: {

  },
  data: {
    expand: false
  },
  methods: {
    expand() {
      this.setData({
        expand: !this.data.expand
      })
    },
    toEditPage(e) {
      let type = e.currentTarget.dataset.type
      wx.navigateTo({
        url: '/subPackages/pages/article/edit/edit?type=' + type,
      })
    }
  }
})
