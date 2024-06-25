const app = getApp()

Page({
  data: {
    prescription: {}
  },
  onLoad(options) {
    console.log(options)
    let id = options.id
    app.request({
      url: `/herb/prescription/info/${id}`
    }).then(res => {
      let prescription = res.data.prescription
      prescription.ingredient = prescription.ingredient
          .replaceAll('#{', '').replaceAll('}', '')
      this.setData({
        prescription: prescription
      })
    })
  }
})