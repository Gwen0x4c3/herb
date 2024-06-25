const app = getApp()

Component({
  properties: {
    showTabBar: {
      type: 'Boolean',
      value: true
    },
    placeholder: {
      type: 'String',
      value: '请输入相关内容'
    },
    name: {
      type: 'String',
      value: ''
    }
  },

  data: {
    uploadImageURL: ''
  },

  methods: {
    _onEditorReady: function () {
      const that = this;
      that.createSelectorQuery().select('#editor').context(function (res) {
        that.editorCtx = res.context
      }).exec()
      this.setData({
        uploadImageURL: app.globalData.baseUrl + '/user/upload/image'
      })
      console.log(app)
    },
    //插入图片
    _addImage: function (event) {
      let _this = this;
      wx.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album'],
        success: function (res) {
          wx.showLoading({
            title: '上传中',
            mask: true
          });
          _this._uploadImage(res.tempFilePaths[0], _this.data.uploadImageURL);
        }
      });
    },
    _uploadImage: function (tempFilePath, uploadImageURL) {
      let _this = this;
      
      wx.uploadFile({
        filePath: tempFilePath,
        name: 'image',
        url: uploadImageURL,
        success: function (res) {
          res = JSON.parse(res.data);
          wx.hideLoading({
            success: () => {
              if (res.code === 200) {
                _this.editorCtx.insertImage({
                  src: app.globalData.baseUrl + res.data.src
                });
              } else {
                wx.showToast({
                  icon: 'error',
                  title: '服务器错误,稍后重试！',
                  mask: true
                })
              }
            },
          });
        }
      });
    },
    //撤销
    _undo: function () {
      this.editorCtx.undo();
    },
    //监控输入
    _onInputting: function (e) {
      let html = e.detail.html;
      let text = e.detail.text;
      this.triggerEvent("input", { html: html, text: text }, {});
    }
  }
})
