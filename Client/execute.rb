require 'tty-prompt'
require_relative 'clients/menu.rb'
require_relative 'agency/agency_menu.rb'

# Menu principal da aplicação (menu dos menus), permite entrar na aplicação
# e escolher entre o menu do cliente ou do vendedor da agência
def display_client_menu
    client_menu = Menu.new
    client_menu.display_menu
end

def display_agency_menu
    agency_menu = AgencyMenu.new
    agency_menu.display_menu
end

@prompt = TTY::Prompt.new

while true
    @prompt.say("\n\t→Menu da Aplicação←")
    chosen_option = @prompt.select("Selecione o modo:", echo: false) do |menu|
        menu.enum '.'
        menu.choice 'Cliente (tem acesso a pesquisa e compra de passagens e hospedagens)', :show_client_menu
        menu.choice 'Vendedor da Agência (pode publicar e deletar passagens e hospedagens)', :show_seller_menu
        menu.choice 'Sair', :exit
    end

    case chosen_option
    when :show_client_menu
        display_client_menu
    when :show_seller_menu
        display_agency_menu
    when :exit
        @prompt.say("Volte novamente!")
        exit
    end
end