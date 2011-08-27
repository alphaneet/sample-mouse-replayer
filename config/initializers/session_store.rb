# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_mouse-replayer_session',
  :secret      => '356497126306279da180f56ec24cdcffcadc05e55ebfeabb4fd893a0a2b4b27cbb9c61f3f19b38975fc6e82b2437d4c2627ea14f2066cc4aa0fb8408c744f683'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
