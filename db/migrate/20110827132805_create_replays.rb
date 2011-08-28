class CreateReplays < ActiveRecord::Migration
  def self.up
    create_table :replays do |t|
      t.string :filename
      t.integer :size
      t.text :xml

      t.timestamps
    end
  end

  def self.down
    drop_table :replays
  end
end
