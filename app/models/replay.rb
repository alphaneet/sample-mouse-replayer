class Replay < ActiveRecord::Base
  def upload_xml=(f)
    self.size = f.size
    self.filename = Time.now.strftime("%Y%m%d-%H%M%S.xml")
    self.xml = f.read
  end
end
